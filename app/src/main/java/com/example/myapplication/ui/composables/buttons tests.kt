package com.example.myapplication.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//@Preview (showBackground = true)
@Composable
fun Layout_exp() {
    Row(
        Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(
            Modifier
                .background(Color.Cyan)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            LabelTry()
            LabelTry2()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyStyledButton( text: String = "Button") {
    
    var counter = remember {
        mutableStateOf(0)
    }
    var index = remember {
        mutableStateOf(0)
    }
    var colors = listOf(Color(0xFF3489EF), Color(0xFF1AE96A), Color(0xFFC9E91A), Color(0xFFE9781A),  Color(0xFFEF3469))
    val color = remember {
        mutableStateOf(Color(0xFF3489EF))
    }
    
    Column(
    
    ) {
        
        Button(
            onClick = {
//                if ((counter.value % 10 == 0) && (color.value == Color(0xFF3489EF))) {
//                    color.value = Color(0xFFEF3469)
//                } else if ((counter.value % 10 == 0) && (color.value == Color(0xFFEF3469))) {
//                    color.value = Color(0xFF3489EF)
//                }
                when (++counter.value) {
                    5 -> index.value = 1
                    10 -> index.value = 2
                    15 -> index.value = 3
                    20 -> index.value = 4
                    25 -> index.value = 0
                    30 -> index.value = 1
                    35 -> index.value = 2
                    40 -> index.value = 3
                    45 -> index.value = 4
                    
                    
                }
                
                color.value = colors[index.value]
            },
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = color.value,
                contentColor = Color(0xFFAAAAAA)
            )
        ) {
            Text(counter.value.toString())
        }
    }
}

@Preview
@Composable
fun ShowText(text: String = "Button") {
    Text(text)
}

@Composable
private fun extracted() {
    Column {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .fillMaxHeight(0.03f)
        )
        LazyColumn(
            Modifier
                .background(Color.Cyan)
                .padding(vertical = 10.dp)
        ) {
            items(count = 500) {
                MyStyledButton()
            }
        }
    }
}