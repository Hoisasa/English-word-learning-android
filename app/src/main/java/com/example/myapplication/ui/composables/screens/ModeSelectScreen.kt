package com.example.myapplication.ui.composables.screens

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.Pink80

@Preview (showBackground = true)
@Composable
fun ModeSelectScreen (
    onModeChosen: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    
    Column(Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
        
    ) {
        Text("Choose mode"
        
        )
        for (text in listOf("Overview", "Pracice", "Exam")) {
            Button( onClick = { onModeChosen(text) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text)
            }
        }

//        val context = LocalContext.current
//        val names = remember { mutableStateListOf<String>() }
//        val translations = remember { mutableStateListOf<String>() }
//        val transcriptions = remember { mutableStateListOf<String>() }
//        val weights = remember { mutableStateListOf<Float>() }
//
//        LaunchedEffect(names, studyGroup) {
//
//            val path = context.getDatabasePath("dictionary.db").absolutePath
//            val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
//
//            val start = System.currentTimeMillis()
//
//            val query = "SELECT word, translation, weight FROM words WHERE subgroup_name = ?"
//            val cursor = db.rawQuery(query, arrayOf(studyGroup))
//
//            val end = System.currentTimeMillis()
//            val elapsedMs = end - start
//            Log.d("Timing", "Query took $elapsedMs ms")
//
//            while (cursor.moveToNext()) {
//                names.add(cursor.getString(0))
//                translations.add(cursor.getString(1))
//
//                weights.add(cursor.getFloat(3))
//            }
//            cursor.close()
//            db.close()
//      }
    }
}