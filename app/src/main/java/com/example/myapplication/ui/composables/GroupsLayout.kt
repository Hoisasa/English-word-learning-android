package com.example.myapplication.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text

@Composable
fun Display_groups(ButtonFunction: (String) -> Unit,
                   groupNames: List<String>,
                   modifier: Modifier = Modifier
) {
    
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(groupNames) { name ->
            Button(
                onClick = { ButtonFunction(name) },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1AE96A),
                    contentColor = Color(0xFF555555),
                ),
            ) {
                Text(name)
            }
        }
    }
}


