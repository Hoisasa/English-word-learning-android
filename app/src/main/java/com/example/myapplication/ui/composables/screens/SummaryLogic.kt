package com.example.myapplication.ui.composables.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.theme.GradeColorHigh
import com.example.myapplication.ui.theme.GradeColorLow
import com.example.myapplication.ui.theme.GradeColorMedium
import com.example.myapplication.ui.theme.GradeColorPerfect

fun getGradeColor(lessonScore: Int): Color {
    return when {
        lessonScore == 100 -> GradeColorPerfect
        lessonScore >= 80 -> GradeColorHigh
        lessonScore >= 60 -> GradeColorMedium
        else -> GradeColorLow
    }
}