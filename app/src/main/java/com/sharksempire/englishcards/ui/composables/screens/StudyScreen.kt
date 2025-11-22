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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sharksempire.englishcards.dao.WordData
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.sharksempire.englishcards.ui.theme.MyRed
import com.sharksempire.englishcards.ui.theme.groupsStyle
import com.sharksempire.englishcards.viewmodels.AbstractLessonViewModel
import kotlinx.coroutines.Job
import kotlin.math.roundToInt

val studyLayout = ConstraintSet {
    val (word, translation, transcription, points,
        correctAnswer, wrongAnswer, actionButton, audio,
        repeat, progressBar) = createRefsFor("word", "translation",
        "transcription", "points", "correctAnswer", "wrongAnswer",
        "actionButton", "audio", "repeat", "progressBar")
    
    val topGuideline = createGuidelineFromTop(0.45f)
    val bottomGuideline = createGuidelineFromBottom(0.25f)
    val startGuideline = createGuidelineFromStart(0.04f)
    val endGuideline = createGuidelineFromEnd(0.04f)
    val topTextGuideline = createGuidelineFromTop(0.3f)
    val bottomTextGuideline = createGuidelineFromTop(0.53f)
    
    constrain(word) {
        top.linkTo(topTextGuideline)
        bottom.linkTo(bottomTextGuideline)
        start.linkTo(startGuideline)
        end.linkTo(endGuideline)
    }
    
    constrain(translation) {
        top.linkTo(word.bottom)
        bottom.linkTo(bottomTextGuideline)
        start.linkTo(word.start)
        end.linkTo(word.end)
    }
    
    constrain(transcription) {
        top.linkTo(topTextGuideline)
        bottom.linkTo(word.top)
        start.linkTo(word.start)
        end.linkTo(word.end)
    }
    
    constrain(points) {
        bottom.linkTo(word.top)
        start.linkTo(startGuideline)
        end.linkTo(endGuideline)
    }
    
    constrain(correctAnswer) {
        top.linkTo(audio.bottom)
        bottom.linkTo(actionButton.top)
        start.linkTo(startGuideline)
        end.linkTo(wrongAnswer.start)
        width = Dimension.fillToConstraints
    }
    
    constrain(wrongAnswer) {
        top.linkTo(repeat.bottom)
        bottom.linkTo(actionButton.top)
        start.linkTo(correctAnswer.end)
        end.linkTo(endGuideline)
        width = Dimension.fillToConstraints
    }
    
    constrain(actionButton) {
        bottom.linkTo(actionButton.top)
        start.linkTo(startGuideline)
        end.linkTo(endGuideline)
        width = Dimension.fillToConstraints
    }
    
    constrain(audio) {
        top.linkTo(topGuideline)
        bottom.linkTo(actionButton.top)
        start.linkTo(startGuideline)
    }
    
    constrain(repeat) {
        top.linkTo(topGuideline)
        bottom.linkTo(actionButton.top)
        end.linkTo(endGuideline)
    }
    
    constrain(progressBar) {
        bottom.linkTo(parent.bottom)
        start.linkTo(startGuideline)
        end.linkTo(endGuideline)
    }
}

val overviewLayout = ConstraintSet {
    val (word, translation, transcription, points,
        correctAnswer, wrongAnswer, actionButton, audio,
        repeat, progressBar) = createRefsFor("word", "translation",
        "transcription", "points", "correctAnswer", "wrongAnswer",
        "actionButton", "audio", "repeat", "progressBar")
    
    val topGuideline = createGuidelineFromTop(0.45f)
    val bottomGuideline = createGuidelineFromBottom(0.25f)
    val startGuideline = createGuidelineFromStart(0.04f)
    val endGuideline = createGuidelineFromEnd(0.04f)
    val topTextGuideline = createGuidelineFromTop(0.3f)
    val bottomTextGuideline = createGuidelineFromTop(0.53f)
    
    constrain(word) {
        top.linkTo(topTextGuideline)
        bottom.linkTo(bottomTextGuideline)
        start.linkTo(startGuideline)
        end.linkTo(endGuideline)
    }
    
    constrain(translation) {
        top.linkTo(word.bottom)
        bottom.linkTo(bottomTextGuideline)
        start.linkTo(word.start)
        end.linkTo(word.end)
    }
    
    constrain(transcription) {
        top.linkTo(topTextGuideline)
        bottom.linkTo(word.top)
        start.linkTo(word.start)
        end.linkTo(word.end)
    }
    
    constrain(points) {
        bottom.linkTo(word.top)
        start.linkTo(startGuideline)
        end.linkTo(endGuideline)
    }
    
    constrain(correctAnswer) {
        width  = Dimension.value(0.dp)
        height = Dimension.value(0.dp)
    }
    
    constrain(wrongAnswer) {
        width  = Dimension.value(0.dp)
        height = Dimension.value(0.dp)
    }
    
    constrain(actionButton) {
        bottom.linkTo(actionButton.top)
        start.linkTo(startGuideline)
        end.linkTo(endGuideline)
        width = Dimension.fillToConstraints
    }
    
    constrain(audio) {
        top.linkTo(topGuideline)
        bottom.linkTo(actionButton.top)
        start.linkTo(startGuideline)
    }
    
    constrain(repeat) {
        top.linkTo(topGuideline)
        bottom.linkTo(actionButton.top)
        end.linkTo(endGuideline)
    }
    
    constrain(progressBar) {
        bottom.linkTo(parent.bottom)
        start.linkTo(startGuideline)
        end.linkTo(endGuideline)
    }
}


sealed interface LessonViewState {
    object Loading: LessonViewState
    data class Error(val message: String): LessonViewState
    data class Success(
        val subGroup: String?,
        val rawWords: List<WordData>,
        val words: List<WordData>,
        val mode: StudyMode = StudyMode.OVER,
        val mistakes: List<WordData> = emptyList(),
        val currentIndex: Int = 0,
        val isTranslationPressed: Boolean = false,
        val isInitComplete: Boolean = false
    ): LessonViewState {
        sealed class StudyMode (
            val displayName: String,
            val viewConstraints: ConstraintSet,
            val onActionClicked: AbstractLessonViewModel.() -> Job,
            val onRestartClicked: AbstractLessonViewModel.() -> Job,
        ) {
            object OVER: StudyMode(
                "Overview",
                overviewLayout,
                AbstractLessonViewModel::setNextIndex,
                AbstractLessonViewModel::resetIndex,
            )
            object PRAC: StudyMode(
                "Practice",
                studyLayout,
                AbstractLessonViewModel::translate,
                AbstractLessonViewModel::prepareLesson,
            )
            object EXAM: StudyMode(
                "Exam",
                studyLayout,
                AbstractLessonViewModel::translate,
                AbstractLessonViewModel::prepareLesson,
            )
            
            object REVW: StudyMode(
                "Review",
                studyLayout,
                AbstractLessonViewModel::translate,
                AbstractLessonViewModel::prepareLesson,
            )
        }
    }
}

@Composable
fun StudyScreen(
    onLessonFinished: () -> Unit,
    mode: LessonViewState.Success.StudyMode,
    viewModel: AbstractLessonViewModel,
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(true) {
        viewModel.setMode(mode)
    }
    
    when (val viewState = state) {
        LessonViewState.Loading -> CircularProgressIndicator(modifier = Modifier.size(50.dp))
        is LessonViewState.Error -> Text(text = viewState.message, fontSize = 20.sp)
        is LessonViewState.Success -> {

            ConstraintLayout(
                constraintSet = viewState.mode.viewConstraints,
                modifier = Modifier.fillMaxSize(),
            ) {
                
                val context = LocalContext.current
                val current = viewModel.getCurrentWord()
                val assetPath = buildAssetFilePath(
                    current.subgroupName,
                    current.word
                )
                
                LaunchedEffect(viewState.currentIndex) {
                    playOggFromAssets(context, assetPath)
                }
                
                Text(
                    "⭐".repeat((current.weight * viewModel.MAX_POINTS).roundToInt()),
                    style = TextStyle(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier
                        .layoutId("points")
                        .padding(bottom = 230.dp)
                )
                
                
                Text(
                    current.word,
                    style = TextStyle(
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier
                        .layoutId("word")
                )
                
                
                Text(
                    "",
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    modifier = Modifier
                        .layoutId("transcription")
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
                            .layoutId("translation")
                    )
                }
                
                Button( onClick = {
                    viewModel.handleAnswer(isCorrect = true)
                    if (viewState.currentIndex == viewState.words.size -1) {
                        onLessonFinished()
                    }
                },
                    modifier = Modifier
                        .layoutId("correctAnswer")
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
                        viewModel.handleAnswer(isCorrect = false)
                        if (viewState.currentIndex == viewState.words.size -1) {
                            onLessonFinished()
                        }
                    },
                    modifier = Modifier
                        .layoutId("wrongAnswer")
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
                    onClick = { viewState.mode.onActionClicked(viewModel) },
                    modifier = Modifier
                        .layoutId("actionButton")
                        .padding(top = 30.dp)
                        .fillMaxHeight(0.08f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MyPurple,
                        contentColor = MyGreenText,
                    ),
                ) {
                    Text(text = when (viewState.mode) {
                        LessonViewState.Success.StudyMode.OVER -> "Next word"
                        else -> "Translate"},
                        style = TextStyle(fontSize = 30.sp))
                }
                
                
                Button(
                    onClick = { playOggFromAssets(context, assetPath) },
                    modifier = Modifier
                        .layoutId("audio")
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
                    onClick = { viewState.mode.onRestartClicked(viewModel) },
                    modifier = Modifier
                        .layoutId("repeat")
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
                
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layoutId("progressBar")
                        .padding(50.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    
                    LinearProgressIndicator(
                        progress = { (viewState.currentIndex +1).toFloat() / viewState.words.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = MyPurple,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                    Text(
                        text = "${viewState.currentIndex + 1} / ${viewState.words.size}",
                        color = MyPurpleShadow,
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = groupsStyle.copy(
                            fontSize = 32.sp,
                            shadow = Shadow(
                                color = Color.White,
                                offset = Offset(0f, 0f),
                                blurRadius = 2f
                            )
                        )
                    )
                }
            }
        }
    }
}
