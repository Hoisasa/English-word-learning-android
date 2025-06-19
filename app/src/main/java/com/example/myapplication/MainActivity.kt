package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Room
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        copyDatabaseFromAssets(applicationContext, "dictionary.db")
        
        
        
        setContent {
            var groupNames by remember { mutableStateOf(emptyList<String>()) }
            
            LaunchedEffect(Unit) {
                val path = applicationContext.getDatabasePath("dictionary.db").absolutePath
                val db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY)
                
                val start = System.currentTimeMillis()
                
                val cursor = db.rawQuery("SELECT name FROM subgroups", null)
                
                
                val end = System.currentTimeMillis()
                val elapsedMs = end - start
                Log.d("Timing", "Query took $elapsedMs ms")
                
                val names = mutableListOf<String>()
                while (cursor.moveToNext()) {
                    names.add(cursor.getString(0))
                }
                cursor.close()
                db.close()
                
                groupNames = names
            }
        
            populate_groups(groupNames)
        }
    }
}

fun copyDatabaseFromAssets(context: Context, dbName: String) {
    val dbPath = context.getDatabasePath(dbName)
    if (!dbPath.exists()) {
        dbPath.parentFile?.mkdirs()
        context.assets.open("databases/$dbName").use { input ->
            dbPath.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}

@Preview
@Composable
fun populate_groups(groupNames: List<String> = listOf("A", "B", "C")) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(groupNames) { name ->
            Button(
                onClick = { Log.d("Buttons", "Clicked $name") },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1AE96A),
                    contentColor = Color(0xFFAAAAAA),
                ),
            ) {
                Text(name)
            }
        }
    }
}