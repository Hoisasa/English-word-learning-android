package com.example.myapplication.ui.composables.screens

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.myapplication.ui.theme.MyGreen
import com.example.myapplication.ui.theme.MyRed
import com.example.myapplication.ui.theme.Pink40

@Composable
fun StudyScreen() {
//    LessonScreenBase()


//    FunctionButton(onCallFunction())


}

@Preview(
    showBackground = true,
    device = Devices.TABLET,
    widthDp = 857,
    heightDp = 1370
)

@Composable
fun StudyScreenPreview() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    
    ) {
//    LessonScreenBase()
        val (word, translation, transcription, points, correctAnswer, wrongAnswer, functionButton, audio, repeat) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.45f)
        val bottomGuideLine = createGuidelineFromBottom(0.25f)
        val startGuideLine = createGuidelineFromStart(0.04f)
        val endGuideLine = createGuidelineFromEnd(0.04f)
        val topTextGuideLine = createGuidelineFromTop(0.3f)
        val bottomTextGuideLine = createGuidelineFromTop(0.53f)
        val studyMode by remember { mutableStateOf("Overview") }
        var isTranslatePressed by remember { mutableStateOf(false) }
        
        
        Text(
            "⭐⭐⭐⭐⭐",
            style = TextStyle(
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier
                .constrainAs(points) {
                    bottom.linkTo(word.top)
                    start.linkTo(startGuideLine)
                    end.linkTo(endGuideLine)
                }
                .padding(bottom = 230.dp)
        )
        
        Text(
            "beginning",
            style = TextStyle(
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .constrainAs(word) {
                    top.linkTo(topTextGuideLine)
                    bottom.linkTo(bottomTextGuideLine)
                    start.linkTo(startGuideLine)
                    end.linkTo(endGuideLine)
                })
        
        val showTranslation = when (studyMode) {
            "Overview" -> true
            else -> isTranslatePressed
        }
        
        if (showTranslation) {
            Text(
                "начало",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier
                    .constrainAs(translation) {
                        top.linkTo(word.bottom)
                        bottom.linkTo(bottomTextGuideLine)
                        start.linkTo(word.start)
                        end.linkTo(word.end)
                    })
            
            Text(
                "[beginin]",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier
                    .constrainAs(transcription) {
                        top.linkTo(topTextGuideLine)
                        bottom.linkTo(word.top)
                        start.linkTo(word.start)
                        end.linkTo(word.end)
                    })
        }
        
        if (studyMode != "Overview") {
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(correctAnswer) {
                        top.linkTo(audio.bottom)
                        bottom.linkTo(functionButton.top)
                        start.linkTo(startGuideLine)
                        end.linkTo(wrongAnswer.start)
                        width = Dimension.fillToConstraints
                    }
                    .padding(end = 120.dp)
                    .fillMaxHeight(0.08f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyGreen,
                    contentColor = Color(0xFF555555),
                ),
            ) {
                Text("Yes", style = TextStyle(fontSize = 30.sp))
            }
            
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(wrongAnswer) {
                        top.linkTo(repeat.bottom)
                        bottom.linkTo(functionButton.top)
                        start.linkTo(correctAnswer.end)
                        end.linkTo(endGuideLine)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 120.dp)
                    .fillMaxHeight(0.08f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyRed,
                    contentColor = Color.White,
                ),
            ) {
                Text("No", style = TextStyle(fontSize = 30.sp))
            }
            
            
            Button(
                onClick = { isTranslatePressed = true },
                modifier = Modifier
                    .constrainAs(functionButton) {
                        top.linkTo(correctAnswer.bottom)
                        bottom.linkTo(bottomGuideLine)
                        start.linkTo(startGuideLine)
                        end.linkTo(endGuideLine)
                        width = Dimension.fillToConstraints
                    }
                    .padding(top = 30.dp)
                    .fillMaxHeight(0.08f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink40,
                    contentColor = Color.White,
                ),
            ) {
                Text("Translate", style = TextStyle(fontSize = 30.sp))
            }
            
            
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(audio) {
                        top.linkTo(topGuideLine)
                        bottom.linkTo(correctAnswer.top)
                        start.linkTo(startGuideLine)
                    }
                    .padding(start = 20.dp, bottom = 10.dp)
                    .width(80.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                ),
            ) {
                Text("🔉", style = TextStyle(fontSize = 30.sp))
            }
            
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(repeat) {
                        top.linkTo(topGuideLine)
                        bottom.linkTo(wrongAnswer.top)
                        end.linkTo(endGuideLine)
                    }
                    .padding(end = 20.dp, bottom = 10.dp)
                    .width(80.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                ),
            ) {
                Text("🔁", style = TextStyle(fontSize = 30.sp))
            }
        } else {
            Button(
                onClick = { },
                modifier = Modifier
                    .constrainAs(functionButton) {
                        bottom.linkTo(functionButton.top)
                        start.linkTo(startGuideLine)
                        end.linkTo(endGuideLine)
                        width = Dimension.fillToConstraints
                    }
                    .padding(top = 30.dp)
                    .fillMaxHeight(0.08f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Pink40,
                    contentColor = Color.White,
                ),
            ) {
                Text("Transaltion", style = TextStyle(fontSize = 30.sp))
            }
            
            
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(audio) {
                        top.linkTo(topGuideLine)
                        bottom.linkTo(functionButton.top)
                        start.linkTo(startGuideLine)
                    }
                    .padding(start = 20.dp, bottom = 10.dp)
                    .width(80.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                ),
            ) {
                Text("🔉", style = TextStyle(fontSize = 30.sp))
            }
            
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(repeat) {
                        top.linkTo(topGuideLine)
                        bottom.linkTo(functionButton.top)
                        end.linkTo(endGuideLine)
                    }
                    .padding(end = 20.dp, bottom = 10.dp)
                    .width(80.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                ),
            ) {
                Text("🔁", style = TextStyle(fontSize = 30.sp))
            }
        }
    }
}
