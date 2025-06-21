package com.example.myapplication.ui.composables.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.persistableBundleOf
import com.example.myapplication.ui.theme.MyGreen
import com.example.myapplication.ui.theme.MyRed
import com.example.myapplication.ui.theme.Pink40
import com.example.myapplication.ui.theme.Pink80
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun OverviewScreen() {
//    LessonScreenBase()


//    FunctionButton(onCallFunction())
    
    
}

@Preview
@Composable
fun OverviewScreenPreview() {
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        
    ) {
//    LessonScreenBase()
        val (word, translation, transcription, points, buttonsBox, correctAnswer, wrongAnswer, functionButton, audio, repeat) = createRefs()
        val bottomGuideLine = createGuidelineFromBottom(0.1f)
        val topGuideLine = createGuidelineFromTop(0.6f)
        val startGuideLine = createGuidelineFromStart(0.0f)
        val endGuideLine = createGuidelineFromEnd(0.0f)
        val topTextGuideLine = createGuidelineFromTop(0.3f)
        val bottomTextGuideLine = createGuidelineFromTop(0.53f)
        
        Text(
            "⭐⭐⭐⭐⭐",
            style = TextStyle(
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier
                .constrainAs(points){
                    bottom.linkTo(transcription.top)
                    start.linkTo(startGuideLine)
                    end.linkTo(endGuideLine)
                }
                .padding(start = 6.dp, bottom = 110.dp)
        )
        
        Text(
            "beginning",
            style = TextStyle(
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .constrainAs(word){
                    top.linkTo(topTextGuideLine)
                    bottom.linkTo(bottomTextGuideLine)
                    start.linkTo(startGuideLine)
                    end.linkTo(endGuideLine)
        })
        
        Text(
            "начало",
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier
                .constrainAs(translation){
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
                .constrainAs(transcription){
                    top.linkTo(topTextGuideLine)
                    bottom.linkTo(word.top)
                    start.linkTo(word.start)
                    end.linkTo(word.end)
        })
        
        
        Box(modifier = Modifier
            .constrainAs(buttonsBox) {
                bottom.linkTo(bottomGuideLine)
                start.linkTo(startGuideLine)
                end.linkTo(endGuideLine)
            }
        )

        Button(
            onClick = {},
            modifier = Modifier
                .constrainAs(correctAnswer) {
                    top.linkTo(audio.bottom)
                    bottom.linkTo(functionButton.top)
                    start.linkTo(startGuideLine)
                }
                .padding(start=20.dp)
                .fillMaxWidth(0.4f)
                .fillMaxHeight(0.05f)
                
            ,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyGreen,
                    contentColor = Color(0xFF555555),
                ),
            ) {
            Text("Yes", style = TextStyle(fontSize = 20.sp))
        }
        
        Button(
            onClick = {},
            modifier = Modifier
                .constrainAs(wrongAnswer) {
                    top.linkTo(repeat.bottom)
                    bottom.linkTo(functionButton.top)
                    end.linkTo(endGuideLine)
                }
                .padding(end=20.dp)
                .fillMaxWidth(0.4f)
                .fillMaxHeight(0.05f)
            
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = MyRed,
                contentColor = Color.White,
            ),
        ) {
            Text("No", style = TextStyle(fontSize = 20.sp))
        }
        
        Button(
            onClick = {},
            modifier = Modifier
                .constrainAs(functionButton) {
                    top.linkTo(correctAnswer.bottom)
                    bottom.linkTo(bottomGuideLine)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(20.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.05f)
            
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = Pink40,
                contentColor = Color.White,
            ),
        ) {
            Text("Translate", style = TextStyle(fontSize = 20.sp))
        }
        
        Button(
            onClick = {},
            modifier = Modifier
                .constrainAs(audio) {
                    top.linkTo(topGuideLine)
                    start.linkTo(startGuideLine)
                }
                .padding(20.dp)
                .width(80.dp)
                .height(80.dp)
            ,
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
                    end.linkTo(endGuideLine)
                }
                .padding(20.dp)
                .width(80.dp)
                .height(80.dp)
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
            ),
        ) {
            Text("🔁", style = TextStyle(fontSize = 30.sp))
        }
//    FunctionButton(onCallFunction())

    
    }

}