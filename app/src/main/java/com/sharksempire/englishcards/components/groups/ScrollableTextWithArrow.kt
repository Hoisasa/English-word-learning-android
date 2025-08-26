package com.sharksempire.englishcards.components.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow

@Composable
fun ScrollableTextWithArrow(text: String, modifier: Modifier = Modifier.Companion, style: TextStyle) {
    val scrollState = rememberScrollState()
    Box(modifier = modifier) {
        Text(
            text = text,
            modifier = Modifier.Companion
                .horizontalScroll(scrollState)
                .padding(top = 15.dp, bottom = 10.dp),
            style = style
        )
        
        // The indicator of a text not fitting in the line
        // is int.MAX_VALUE on recomposition -> we cut that one out to avoid flickery
        
        if (scrollState.maxValue != Int.MAX_VALUE && (scrollState.value + 10) < scrollState.maxValue) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = MyGreen,
                contentDescription = "Scroll right",
                modifier = Modifier.Companion
                    .align(Alignment.Companion.CenterEnd)
                    .size(50.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    
                    .background(MyPurpleShadow.copy(alpha = 0.7f))
            )
        }
    }
}