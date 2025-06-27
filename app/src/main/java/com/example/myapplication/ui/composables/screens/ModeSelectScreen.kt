package com.example.myapplication.ui.composables.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.LightPurple
import com.example.myapplication.ui.theme.MyGreen
import com.example.myapplication.ui.theme.MyGreenText
import com.example.myapplication.ui.theme.MyPurple
import com.example.myapplication.ui.theme.MyPurpleShadow

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
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 34.dp, end = 34.dp)// fill the parent container
                .wrapContentSize(Alignment.Center)  // center content inside Box
        ) {
            Text(
                "Choose mode",
                color = MyGreen,
                modifier = Modifier.padding(start = 40.dp),
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold,
                shadow = Shadow(
                    color = MyPurpleShadow,
                    offset = Offset(-6f, 6f),  // adjust for shadow position
                    blurRadius = 4f           // adjust for softness
                ))
            )
            for (text in listOf("Overview", "Practice", "Exam")) {
                
                Button(
                    onClick = { onModeChosen(text) },
                    shape = RoundedCornerShape(40.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp).height(75.dp)
                        .shadow(6.dp, shape = RoundedCornerShape(40.dp)), // shadow with rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MyPurple,
                        contentColor = MyGreenText,
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 30.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                if (text.isNotEmpty()) {
                                    withStyle(style = SpanStyle(color = LightPurple)) {
                                        append(text[0])
                                    }
                                    withStyle(style = SpanStyle(color = MyGreen)) {
                                        append(text.substring(1))
                                    }
                                }
                            },
                            modifier = Modifier.padding(top = 15.dp),
                            style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.SemiBold,
                                shadow = Shadow(
                                    color = MyPurpleShadow,
                                    offset = Offset(-6f, -6f),  // adjust for shadow position
                                    blurRadius = 4f           // adjust for softness
                                ))
                        )
                    }
                }
            }
        }
    }
}