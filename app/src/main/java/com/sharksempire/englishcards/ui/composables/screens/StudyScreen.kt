package com.sharksempire.englishcards.ui.composables.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyRed

@Composable
fun StudyScreen(
    studyMode: String,
    lessonWords: List<WordData>,
    lessonMistakes: SnapshotStateList<WordData>,
    endOfLesson: (Int) -> Unit,
    restart: () -> Unit,
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (word, translation, transcription, points, correctAnswer, wrongAnswer, functionButton, audio, repeat, progressBar) = createRefs()
        val topGuideline = createGuidelineFromTop(0.45f)
        val bottomGuideline = createGuidelineFromBottom(0.25f)
        val startGuideline = createGuidelineFromStart(0.04f)
        val endGuideline = createGuidelineFromEnd(0.04f)
        val topTextGuideline = createGuidelineFromTop(0.3f)
        val bottomTextGuideline = createGuidelineFromTop(0.53f)
        var currentWordIndex by rememberSaveable { mutableIntStateOf(0) }
        var isTranslatePressed by rememberSaveable { mutableStateOf(false) }
        
        val context = LocalContext.current
        val assetPath = buildAssetFilePath(lessonWords[currentWordIndex].subgroup, lessonWords[currentWordIndex].word)

        
        Text(
            showPoints(lessonWords[currentWordIndex].weight),
            style = TextStyle(
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier
                .constrainAs(points) {
                    bottom.linkTo(word.top)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                }
                .padding(bottom = 230.dp)
        )
        
        var lastPlayedIndex by rememberSaveable { mutableIntStateOf(-1) }
        
        LaunchedEffect(currentWordIndex) {
            if (currentWordIndex != lastPlayedIndex) {
                playOggFromAssets(context, assetPath)
                lastPlayedIndex = currentWordIndex
            }
        }
        
        Text(
            lessonWords[currentWordIndex].word,
            style = TextStyle(
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .constrainAs(word) {
                    top.linkTo(topTextGuideline)
                    bottom.linkTo(bottomTextGuideline)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                }
        )
        
        val showTranslation = when (studyMode) {
            "Overview" -> true
            else -> isTranslatePressed
        }
        
        if (showTranslation) {
            Text(
                lessonWords[currentWordIndex].translation,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier
                    .constrainAs(translation) {
                        top.linkTo(word.bottom)
                        bottom.linkTo(bottomTextGuideline)
                        start.linkTo(word.start)
                        end.linkTo(word.end)
                    }
            )
            
            Text(
                "",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier
                    .constrainAs(transcription) {
                        top.linkTo(topTextGuideline)
                        bottom.linkTo(word.top)
                        start.linkTo(word.start)
                        end.linkTo(word.end)
                    }
            )
        }
        
        if (studyMode != "Overview") {
            Button(
                onClick = {
                    updateWeight(isCorrect = true, isExam = studyMode, lessonWords[currentWordIndex].id, context)
                    if (currentWordIndex < lessonWords.size - 1) {
                        isTranslatePressed = false
                        currentWordIndex++
                    } else {
                        currentWordIndex = 0
                        endOfLesson(calculateGrade(lessonMistakes.size, lessonWords.size)
                        )
                    }
                },
                modifier = Modifier
                    .constrainAs(correctAnswer) {
                        top.linkTo(audio.bottom)
                        bottom.linkTo(functionButton.top)
                        start.linkTo(startGuideline)
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
                onClick = {
                    updateWeight(isCorrect = false, isExam = studyMode, lessonWords[currentWordIndex].id, context)
                    lessonMistakes.add(lessonWords[currentWordIndex])
                    if (currentWordIndex < lessonWords.size - 1) {
                        isTranslatePressed = false
                        currentWordIndex++
                    } else {
                        currentWordIndex = 0
                        endOfLesson(calculateGrade(lessonMistakes.size, lessonWords.size))
                    }
                },
                modifier = Modifier
                    .constrainAs(wrongAnswer) {
                        top.linkTo(repeat.bottom)
                        bottom.linkTo(functionButton.top)
                        start.linkTo(correctAnswer.end)
                        end.linkTo(endGuideline)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 120.dp)
                    .fillMaxHeight(0.08f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyRed,
                    contentColor = MyGreenText,
                ),
            ) {
                Text("No", style = TextStyle(fontSize = 30.sp))
            }
            
            
            Button(
                onClick = { isTranslatePressed = true },
                modifier = Modifier
                    .constrainAs(functionButton) {
                        top.linkTo(correctAnswer.bottom)
                        bottom.linkTo(bottomGuideline)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        width = Dimension.fillToConstraints
                    }
                    .padding(top = 30.dp)
                    .fillMaxHeight(0.08f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyPurple,
                    contentColor = MyGreenText,
                ),
            ) {
                Text("Translate", style = TextStyle(fontSize = 30.sp))
            }
            
            
            Button(
                onClick = {
                    
                    playOggFromAssets(context, assetPath) },
                modifier = Modifier
                    .constrainAs(audio) {
                        top.linkTo(topGuideline)
                        bottom.linkTo(correctAnswer.top)
                        start.linkTo(startGuideline)
                    }
                    .padding(start = 20.dp, bottom = 10.dp)
                    .width(80.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MyGreenText,
                ),
            ) {
                Text("🔉", style = TextStyle(fontSize = 30.sp))
            }
            
            Button(
                onClick = {
                    currentWordIndex = 0 // resets index in the native scope
                    restart() }, // sets restartRequested to true
                modifier = Modifier
                    .constrainAs(repeat) {
                        top.linkTo(topGuideline)
                        bottom.linkTo(wrongAnswer.top)
                        end.linkTo(endGuideline)
                    }
                    .padding(end = 20.dp, bottom = 10.dp)
                    .width(80.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MyGreenText,
                ),
            ) {
                Text("🔁", style = TextStyle(fontSize = 30.sp))
            }
        } else {
            Button(
                onClick = { if (currentWordIndex < lessonWords.size -1) {
                    currentWordIndex++
                } else {
                    currentWordIndex = 0
                } },
                modifier = Modifier
                    .constrainAs(functionButton) {
                        bottom.linkTo(functionButton.top)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        width = Dimension.fillToConstraints
                    }
                    .padding(top = 30.dp)
                    .fillMaxHeight(0.08f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MyPurple,
                    contentColor = MyGreenText,
                ),
            ) {
                Text("Next Word", style = TextStyle(fontSize = 30.sp))
            }
            
            
            Button(
                onClick = {playOggFromAssets(context, assetPath)},
                modifier = Modifier
                    .constrainAs(audio) {
                        top.linkTo(topGuideline)
                        bottom.linkTo(functionButton.top)
                        start.linkTo(startGuideline)
                    }
                    .padding(start = 20.dp, bottom = 10.dp)
                    .width(80.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MyGreenText,
                ),
            ) {
                Text("🔉", style = TextStyle(fontSize = 30.sp))
            }
            
            Button(
                onClick = { currentWordIndex = 0 },
                modifier = Modifier
                    .constrainAs(repeat) {
                        top.linkTo(topGuideline)
                        bottom.linkTo(functionButton.top)
                        end.linkTo(endGuideline)
                    }
                    .padding(end = 20.dp, bottom = 10.dp)
                    .width(80.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MyGreenText,
                ),
            ) {
                Text("🔁", style = TextStyle(fontSize = 30.sp))
            }
        }
        
        Box(modifier = Modifier.fillMaxWidth().constrainAs(progressBar) {
            bottom.linkTo(parent.bottom)
            start.linkTo(startGuideline)
            end.linkTo(endGuideline)
        }.padding(50.dp),
            contentAlignment = Alignment.BottomCenter) {
            
            LinearProgressIndicator(
                progress = { currentWordIndex.toFloat() / lessonWords.size },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                color = MyPurple,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                
                )
        }
        
    }
    

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
        val studyMode by rememberSaveable { mutableStateOf("Practice") }
        var isTranslatePressed by rememberSaveable { mutableStateOf(false) }
        val wordScore by rememberSaveable { mutableStateOf("⭐⭐⭐⭐⭐") }
        
        Text(
            wordScore,
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
                "[beginning]",
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
                    contentColor = MyGreenText,
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
                    containerColor = MyPurple,
                    contentColor = MyGreenText,
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
                    contentColor = MyGreenText,
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
                    contentColor = MyGreenText,
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
                    containerColor = MyPurple,
                    contentColor = MyGreenText,
                ),
            ) {
                Text("Translation", style = TextStyle(fontSize = 30.sp))
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
                    contentColor = MyGreenText,
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
                    contentColor = MyGreenText,
                ),
            ) {
                Text("🔁", style = TextStyle(fontSize = 30.sp))
            }
        }
    }
}
