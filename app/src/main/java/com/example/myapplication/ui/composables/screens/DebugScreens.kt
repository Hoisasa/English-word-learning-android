
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext


@Composable
fun AudioDebugScreen() {
    val context = LocalContext.current
    
    // Run this once on composition
    LaunchedEffect(Unit) {
        val dbPath = context.getDatabasePath("dictionary.db").absolutePath
        val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        
        // Query to get subgroups and their word counts
        val subgroupWordCounts = mutableListOf<SubgroupInfo>()
        val cursor = db.rawQuery("""
            SELECT subgroups.name, subgroups.group_id, COUNT(words.id) AS word_count
            FROM subgroups
            LEFT JOIN words ON subgroups.name = words.subgroup_name
            GROUP BY subgroups.name
        """.trimIndent(), null)
        
        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val groupId = cursor.getString(1)
            val wordCount = cursor.getInt(2)
            subgroupWordCounts.add(SubgroupInfo(name, groupId, wordCount))
        }
        cursor.close()
        
        // Check files in assets for each subgroup folder and compare counts
        val assets = context.assets
        
        for (subgroup in subgroupWordCounts) {
            val safeName = folderNameFromGroup(subgroup.name)
            try {
                val files = assets.list("audiofiles/${safeName}") ?: arrayOf()
                if (files.size != subgroup.wordCount) {
                    Log.w("AudioDebug", "Subgroup '${subgroup.name}' expected ${subgroup.wordCount} audio files, found ${files.size}")
                }
                
//                // Now check words in this subgroup without corresponding audio files
//                val missingAudioWords = mutableListOf<String>()
//                val wordCursor = db.rawQuery("SELECT word FROM words WHERE subgroup_name = ?", arrayOf(subgroup.name))
//                while (wordCursor.moveToNext()) {
//                    val word = wordCursor.getString(0)
//                    val expectedFileName = "$word.wav" // or .wav, adapt accordingly
//                    if (!files.contains(expectedFileName)) {
//                        missingAudioWords.add(word)
//                    }
//                }
//                wordCursor.close()
//
//                if (missingAudioWords.isNotEmpty()) {
//                    Log.w("AudioDebug", "Words missing audio in subgroup '${subgroup.name}': $missingAudioWords")
//                }
            } catch (e: Exception) {
                Log.e("AudioDebug", "Error checking subgroup '${subgroup.name}': ${e.message}")
            }
        }
        
        db.close()
    }
}

fun folderNameFromGroup(group: String): String {
    return group.replace("/", "-").replace(":", "-")
}

// Data class to hold subgroup info
data class SubgroupInfo(val name: String, val groupId: String, val wordCount: Int)
