package com.example.myapplication.ui.composables.screens
import androidx.compose.material3.Text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import com.example.myapplication.ui.theme.MyBlue
import com.example.myapplication.ui.theme.MyGreen
import com.example.myapplication.ui.theme.MyRed

@Composable
fun OverviewScreen() {
//    LessonScreenBase()


//    FunctionButton(onCallFunction())
    
    
}

@Preview (
    device = Devices.TABLET,
    widthDp = 857,
    heightDp = 1370)

@Composable
fun SummaryScreenPreview() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    
    ) {
        val (grade, gradeMark, mistakes, back, repeat, save) = createRefs()
        val topGuideLine = createGuidelineFromTop(0.04f)
        val bottomGuideLine = createGuidelineFromBottom(0.25f)
        val bottomMistakesGuideLine = createGuidelineFromBottom(0.08f)
        val startGuideLine = createGuidelineFromStart(0.04f)
        val endGuideLine = createGuidelineFromEnd(0.04f)
        val studyMode by remember { mutableStateOf("Practice") }
        
        
        Text(
            "Grade",
            style = TextStyle(
                fontSize = 200.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .constrainAs(grade) {
                    top.linkTo(topGuideLine)
                    start.linkTo(startGuideLine)
                    end.linkTo(endGuideLine)
                })
        
        Text(
            "GradeHistory",
            style = TextStyle(
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .constrainAs(gradeMark) {
                    top.linkTo(grade.bottom)
                    start.linkTo(startGuideLine)
                    end.linkTo(endGuideLine)
                }
                .padding(bottom = 20.dp))
        
        LazyColumn(
            modifier = Modifier
                .constrainAs(mistakes) {
                    top.linkTo(gradeMark.bottom)
                    bottom.linkTo(bottomMistakesGuideLine)
                    start.linkTo(startGuideLine)
                    end.linkTo(endGuideLine)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .clip(RoundedCornerShape(60.dp))
                .background(Color.LightGray),
            horizontalAlignment = Alignment.CenterHorizontally,
            ) { items(listOf("fsdsjflsdjfls", "jfskfjsdlfjskf", "sfklajflsajfaskjf", "fjslkfjslsfjsf")) {
                name ->
                Text(name,
                    style = TextStyle(
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                ),)
            }
        }
        
        Button(
            onClick = {},
            modifier = Modifier
                .constrainAs(back) {
                    top.linkTo(mistakes.top)
                    bottom.linkTo(bottomGuideLine)
                    start.linkTo(parent.start)
                    height = Dimension.fillToConstraints
                }
            .padding(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MyRed,
                contentColor = Color.White,
            ),
        ) {
            Text("Back",
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.graphicsLayer {
                    rotationZ = -90f
                })
        }
        if (studyMode == "Practice") {
            
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(repeat) {
                        top.linkTo(mistakes.top)
                        bottom.linkTo(bottomGuideLine)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
                .padding(10.dp),
            colors = ButtonDefaults.buttonColors(
                    containerColor = MyBlue,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    "🔁",
                    style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.graphicsLayer {
                        rotationZ = -90f
                    })
            }
        } else {
            
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(repeat) {
                        top.linkTo(mistakes.top)
                        bottom.linkTo(save.top)
                        end.linkTo(parent.end)
                        start.linkTo(save.start)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .padding(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyBlue,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    "🔁",
                    style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold))
            }
            
            Button(
                onClick = {},
                modifier = Modifier
                    .constrainAs(save) {
                        top.linkTo(repeat.bottom)
                        bottom.linkTo(bottomGuideLine)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
                    .padding(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyBlue,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    "Save",
                    style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold))
            }
        }
    }
}