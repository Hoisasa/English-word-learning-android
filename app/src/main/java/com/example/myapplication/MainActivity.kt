package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.composables.MyEnglishApp

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        copyDatabaseFromAssets(applicationContext, "dictionary.db")
        
        setContent {
            MyEnglishApp(modifier = Modifier.fillMaxSize())
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

