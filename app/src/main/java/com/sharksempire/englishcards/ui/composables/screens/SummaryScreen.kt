package com.sharksempire.englishcards.ui.composables.screens

import android.database.sqlite.SQLiteDatabase
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sharksempire.englishcards.components.groups.ScrollableTextWithArrow
import com.sharksempire.englishcards.ui.theme.GradeColorHigh
import com.sharksempire.englishcards.ui.theme.GradeColorLow
import com.sharksempire.englishcards.ui.theme.GradeColorMedium
import com.sharksempire.englishcards.ui.theme.GradeColorPerfect
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.sharksempire.englishcards.ui.theme.MyRed
import com.sharksempire.englishcards.ui.theme.PurpleGrey40
import com.sharksempire.englishcards.ui.theme.summaryStyle
import com.sharksempire.englishcards.viewmodels.LessonViewModel

@Composable
fun SummaryScreen(
    onSaveClicked: () -> Unit,
    restart: () -> Unit = {},
    viewModel: LessonViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    
    when (val viewState = state) {
        LessonViewState.Loading -> CircularProgressIndicator(modifier = Modifier.size(50.dp))
        is LessonViewState.Error -> Text(text = viewState.message)
        is LessonViewState.Success -> {
            
            val lessonScore = ((viewState.mistakes.size.toFloat() / viewState.words.size) * 100).toInt()
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
                
                Text(
                    lessonScore.toString(),
                    style = TextStyle(
                        fontSize = 200.sp,
                        fontWeight = FontWeight.Bold,
                        color = getGradeColor(lessonScore),
                        shadow = Shadow(
                            color = MyPurpleShadow,
                            offset = Offset(0f, -16f),  // adjust for shadow position
                            blurRadius = 16f           // adjust for softness
                        )
                    ),
                    modifier = Modifier
                        .constrainAs(grade) {
                            top.linkTo(topGuideLine)
                            start.linkTo(startGuideLine)
                            end.linkTo(endGuideLine)
                        })
                

                
                LazyRow(
                    modifier = Modifier
                        .constrainAs(gradeMark) {
                            top.linkTo(grade.bottom)
                            start.linkTo(startGuideLine)
                            end.linkTo(endGuideLine)
                        }
                        .padding(bottom = 20.dp),
                    
                    ) {
                }
                
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
                        .background(PurpleGrey40),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    items(
                        viewState.mistakes
                    ) { mistake ->
                        
                        Column(
                            modifier = Modifier.padding(start = 100.dp, end = 100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            for (item in listOf(mistake.word, mistake.translation)) {
                                ScrollableTextWithArrow(
                                    item,
                                    style = summaryStyle,
                                )
                            }
                        }
                        
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = getGradeColor(lessonScore).copy(alpha = 0.5f),
                            modifier = Modifier
                                .padding(start = 150.dp, end = 150.dp)
                        )
                    }
                }
                
                Button(
                    onClick = onSaveClicked,
                    modifier = Modifier
                        .constrainAs(back) {
                            top.linkTo(mistakes.top)
                            bottom.linkTo(bottomGuideLine)
                            start.linkTo(parent.start)
                            width = Dimension.value(135.dp)
                            height = Dimension.fillToConstraints
                        }
                        .padding(10.dp)
                        .shadow(
                            6.dp,
                            shape = RoundedCornerShape(40.dp)
                        ), // shadow with rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MyPurple,
                        contentColor = MyGreenText,
                    ),
                ) {
                    Text(
                        "Quit",
                        modifier = Modifier.graphicsLayer {
                            rotationZ = -90f
                        },
                        style = TextStyle(
                            fontSize = 20.sp, fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = MyPurpleShadow,
                                offset = Offset(-6f, 6f),  // adjust for shadow position
                                blurRadius = 4f           // adjust for softness
                            )
                        )
                    )
                }
                
                if (viewState.mode == LessonViewState.Success.StudyMode.PRAC) {
                    Button(
                        onClick = { restart() },
                        modifier = Modifier
                            .constrainAs(repeat) {
                                top.linkTo(mistakes.top)
                                bottom.linkTo(bottomGuideLine)
                                end.linkTo(parent.end)
                                width = Dimension.value(135.dp)
                                height = Dimension.fillToConstraints
                            }
                            .padding(10.dp)
                            .shadow(
                                6.dp,
                                shape = RoundedCornerShape(40.dp)
                            ), // shadow with rounded corners
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MyPurple,
                            contentColor = MyGreenText,
                        ),
                    ) {
                        Text(
                            "🔁",
                            style = TextStyle(
                                fontSize = 30.sp, fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = MyPurpleShadow,
                                    offset = Offset(3f, 6f),  // adjust for shadow position
                                    blurRadius = 4f           // adjust for softness
                                )
                            ),
                        )
                    }
                    
                } else {
                    
                    
                    Button(
                        onClick = {
                            restart()
                        },
                        modifier = Modifier
                            .constrainAs(repeat) {
                                top.linkTo(mistakes.top)
                                if (viewState.mistakes.size <= 1) {
                                    bottom.linkTo(save.top)
                                } else {
                                    bottom.linkTo(bottomGuideLine)
                                }
                                end.linkTo(parent.end)
                                width = Dimension.value(135.dp)
                                height = Dimension.fillToConstraints
                            }
                            .padding(10.dp)
                            .shadow(
                                6.dp,
                                shape = RoundedCornerShape(40.dp)
                            ), // shadow with rounded corners
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MyPurple,
                            contentColor = MyGreenText,
                        ),
                    ) {
                        Text(
                            "🔁",
                            style = TextStyle(
                                fontSize = 30.sp, fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = MyPurpleShadow,
                                    offset = Offset(3f, 6f),  // adjust for shadow position
                                    blurRadius = 4f           // adjust for softness
                                )
                            )
                        )
                    }
                    
                    if (viewState.mistakes.size <= 1) {
                        Button(
                            onClick = {
                            },
                            modifier = Modifier
                                .constrainAs(save) {
                                    top.linkTo(repeat.bottom)
                                    bottom.linkTo(bottomGuideLine)
                                    end.linkTo(parent.end)
                                    height = Dimension.fillToConstraints
                                    width = Dimension.value(135.dp)
                                    
                                }
                                .padding(10.dp)
                                .shadow(
                                    6.dp,
                                    shape = RoundedCornerShape(40.dp)
                                ), // shadow with rounded corners
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MyPurple,
                                contentColor = MyGreenText,
                            ),
                        ) {
                            Text(
                                "Save",
                                style = TextStyle(
                                    fontSize = 22.sp, fontWeight = FontWeight.Bold,
                                    shadow = Shadow(
                                        color = MyPurpleShadow,
                                        offset = Offset(-6f, 3f),  // adjust for shadow position
                                        blurRadius = 4f           // adjust for softness
                                    )
                                ),
                                modifier = Modifier.graphicsLayer {
                                    rotationZ = -90f
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getGradeColor(lessonScore: Int): Color {
    return when {
        lessonScore == 100 -> GradeColorPerfect
        lessonScore >= 80 -> GradeColorHigh
        lessonScore >= 60 -> GradeColorMedium
        else -> GradeColorLow
    }
}