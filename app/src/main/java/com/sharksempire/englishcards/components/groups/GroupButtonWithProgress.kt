package com.sharksempire.englishcards.components.groups

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharksempire.englishcards.ui.composables.Item
import com.sharksempire.englishcards.ui.composables.screens.ScrollableTextWithArrow
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.sharksempire.englishcards.ui.theme.groupsStyle

@Composable
fun GroupButtonWithProgress(
    onClick: (String) -> Unit,
    group: Item, colors: List<Color>,
    modifier: Modifier = Modifier,
    ) {
    val groupProgress = (group as? Item.GroupsWithProgressData)
    Button(
        onClick = { onClick(group.name) },
        shape = RoundedCornerShape(40.dp),
        modifier = modifier.fillMaxWidth()
            .padding(top = 20.dp)
            .shadow(
                6.dp,
                shape = RoundedCornerShape(40.dp)
            ),
        // shadow with rounded corners
        colors = ButtonDefaults.buttonColors(
            containerColor = colors[0],
            contentColor = colors[1],
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(6.dp, shape = CircleShape)
            ) {
                CircularProgressIndicator(
                    progress = { groupProgress!!.learned.toFloat() / groupProgress.total },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 13.dp,
                    strokeCap = StrokeCap.Round,
                    color = colors[2]
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "${groupProgress!!.learned}/${groupProgress.total}",
                modifier = Modifier.weight(1f),
                color = colors[1],
                style = TextStyle(
                    fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(
                        color = colors[3],
                        offset = Offset(-6f, 6f),  // adjust for shadow position
                        blurRadius = 4f           // adjust for softness
                    )
                )
            )
            
            ScrollableTextWithArrow(
                text = group.name,
                modifier = Modifier
                    .weight(10f),
                style = groupsStyle
            )
        }
    }
}

@Preview(widthDp = 800)
@Composable
fun PreviewGroupButton() {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item{
            GroupButtonWithProgress(
                onClick = { },
                group = Item.GroupsWithProgressData(
                    name = "Интервальное повторение",
                    learned = 0,
                    total = 1,
                    pos = "outclass"
                ),
                colors = listOf(MyGreen, MyPurpleShadow, MyGreen, MyGreenText),
            )
        }
    }
}