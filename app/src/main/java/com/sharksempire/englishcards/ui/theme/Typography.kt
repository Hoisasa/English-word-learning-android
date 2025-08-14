package com.sharksempire.englishcards.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val summaryStyle = TextStyle(
    fontSize = 50.sp,
    fontWeight = FontWeight.Bold,
    color = MyPurpleShadow,
    shadow = Shadow(
        color = MyGreen,
        offset = Offset(-6f, -6f),  // adjust for shadow position
        blurRadius = 4f           // adjust for softness
    )
)

val groupsStyle = TextStyle(
    fontSize = 24.sp, fontWeight = FontWeight.SemiBold,
    shadow = Shadow(
        color = MyPurpleShadow,
        offset = Offset(-6f, 6f),  // adjust for shadow position
        blurRadius = 4f           // adjust for softness
    )
)