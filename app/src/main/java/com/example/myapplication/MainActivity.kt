package com.example.myapplication

import com.example.myapplication.ui.composables.MyEnglishApp
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.Pink80

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

