package com.sharksempire.englishcards.ui.composables.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import com.sharksempire.englishcards.dao.WordData
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyRed
import com.sharksempire.englishcards.viewmodels.LessonViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


sealed interface LessonViewState {
    object Loading: LessonViewState
    data class Error(val message: String): LessonViewState
    data class Success(
        val subGroup: String,
        val words: List<WordData>,
        val mode: StudyMode = StudyMode.UNKN,
        val mistakes: List<WordData> = emptyList(),
        val currentIndex: Int = 0,
        val isTranslationPressed: Boolean = false,
    ): LessonViewState {
        @Serializable
        sealed class StudyMode(val displayName: String) {
            @Serializable
            object OVER: StudyMode("Overview") // Overview
            @Serializable
            object PRAC: StudyMode("Practice") // Practice
            @Serializable
            object EXAM: StudyMode("Exam") // Exam
            @Serializable
            object UNKN: StudyMode("ERROR!! ERROR!!") // Unknown
        }
        
        object ModeNavType {
            val ModeType = object : NavType<StudyMode> (isNullableAllowed = false) {
                override fun get( bundle: SavedState, key: String): StudyMode? {
                    return Json.decodeFromString(bundle.getString(key) ?: return null)
                }
                
                override fun parseValue(value: String): StudyMode {
                    return Json.decodeFromString(value)
                }
                
                override fun serializeAsValue(value: StudyMode): String {
                    return Json.encodeToString(value)
                }
                
                override fun put( bundle: SavedState, key: String, value: StudyMode ) {
                    bundle.putString(key, Json.encodeToString(value))
                }
            }
        }
    }
}

@Composable
fun StudyScreen(
    onLessonFinished: () -> Unit,
    mode: LessonViewState.Success.StudyMode,
    viewModel: LessonViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit, block = {
        viewModel.setMode(mode)
    })
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    when (val viewState = state) {
        LessonViewState.Loading -> CircularProgressIndicator(modifier = Modifier.size(50.dp))
        is LessonViewState.Error -> Text(text = viewState.message)
        is LessonViewState.Success -> {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (word, translation, transcription, points, correctAnswer, wrongAnswer, functionButton, audio, repeat, progressBar) = createRefs()
                val topGuideline = createGuidelineFromTop(0.45f)
                val bottomGuideline = createGuidelineFromBottom(0.25f)
                val startGuideline = createGuidelineFromStart(0.04f)
                val endGuideline = createGuidelineFromEnd(0.04f)
                val topTextGuideline = createGuidelineFromTop(0.3f)
                val bottomTextGuideline = createGuidelineFromTop(0.53f)
                
                val context = LocalContext.current
                val current = viewModel.getCurrentWord()
                val assetPath = buildAssetFilePath(
                    viewState.subGroup,
                    current.word
                )
                
                
                Text(
                    showPoints(current.weight),
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
                
                LaunchedEffect(viewState.currentIndex) {
                    if (viewState.currentIndex != lastPlayedIndex) {
                        playOggFromAssets(context, assetPath)
                        lastPlayedIndex = viewState.currentIndex
                    }
                }
                
                Text(
                    current.word,
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

                val showTranslation = when (viewState.mode) {
                    LessonViewState.Success.StudyMode.OVER -> true
                    else -> viewState.isTranslationPressed
                }
                
                if (showTranslation) {
                    Text(
                        current.translation,
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
                }
                
                if (viewState.mode != LessonViewState.Success.StudyMode.OVER) {
                    Button( onClick = {
                            val isEnd = viewState.currentIndex == viewState.words.size -1
                            viewModel.handleAnswer(isCorrect = true, isEnd)
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
                            val isEnd = viewState.currentIndex == viewState.words.size -1
                            viewModel.handleAnswer(isCorrect = false, isEnd)
                            if (isEnd) { onLessonFinished() }
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
                        onClick = { viewModel.translate() },
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
                            
                            playOggFromAssets(context, assetPath)
                        },
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

                        }, // sets restartRequested to true
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
                        onClick = {

                        },
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
                        onClick = { playOggFromAssets(context, assetPath) },
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
                        onClick = { },
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
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(progressBar) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(startGuideline)
                            end.linkTo(endGuideline)
                        }
                        .padding(50.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    
                    LinearProgressIndicator(
                        progress = { viewState.currentIndex.toFloat() / viewState.words.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = MyPurple,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                        
                        )
                }
                
            }
        }
    }
}


