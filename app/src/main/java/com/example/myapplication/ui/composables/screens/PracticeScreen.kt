package com.example.myapplication.ui.composables.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun PracticeScreen(
    onBack: () -> Unit,
    name: String = "beginning",
    translation: String = "начало",
    weight: Float = 1f,
) {
    Column () {
        Text(name, fontSize = 24.sp)
        Text(translation, fontSize = 24.sp)
        Text(weight.toString(), fontSize = 24.sp)
    }
    
    BackHandler {
        onBack()
    }
}