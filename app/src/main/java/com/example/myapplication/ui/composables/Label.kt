package com.example.myapplication.ui.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun LabelTry(

) {
    
    Text("Hello Raff!", textAlign = TextAlign.Center, fontSize = 32.sp)
}

@Preview(showBackground = true)
@Composable
fun LabelTry2() {
    Text("Hello Alex!", textAlign = TextAlign.Center, fontSize = 32.sp)
}