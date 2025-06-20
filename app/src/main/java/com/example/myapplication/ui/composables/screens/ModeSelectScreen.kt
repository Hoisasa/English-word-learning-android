package com.example.myapplication.ui.composables.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ModeSelectScreen (
    onModeChosen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(Modifier.fillMaxWidth()) {
        Button( onClick = {} ) { }
    }
}