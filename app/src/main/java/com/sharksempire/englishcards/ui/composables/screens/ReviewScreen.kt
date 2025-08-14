package com.sharksempire.englishcards.ui.composables.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.sharksempire.englishcards.ui.theme.groupsStyle
import com.sharksempire.englishcards.ui.theme.summaryStyle

@Composable
fun ReviewScreen(
    subgroupsToReview: List<GroupsWithProgressData>,
    buttonFunction: (GroupsWithProgressData) -> Unit = {},
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        for (i in 1..5) {
            item {
                Text("Level $i",
                    modifier = Modifier.padding(top = 40.dp),
                    style = summaryStyle.copy(fontSize = 24.sp))
                Text(
                    "repeat after ${
                        when (i) {
                            1 -> 3
                            2 -> 12
                            3 -> 24
                            4 -> 60
                            5 -> 180
                            else -> 1
                        }
                    } days",
                    style = summaryStyle.copy(fontSize = 24.sp)
                )
            }
            
            items(
                items = subgroupsToReview.filter { it.level == i },
                key = { group -> group.name }
            ) { group ->
                
                Button(
                    onClick = { buttonFunction(group) },
                    shape = RoundedCornerShape(40.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .shadow(
                            6.dp,
                            shape = RoundedCornerShape(40.dp)
                        )
                        .animateItem(),
                    // shadow with rounded corners
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MyPurple,
                        contentColor = MyGreenText,
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
                                progress = { group.learned.toFloat() / group.total },
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 13.dp,
                                strokeCap = StrokeCap.Round,
                                color = MyGreen
                            )
                        }
                        
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "${group.learned}/${group.total}",
                            modifier = Modifier.weight(1f),
                            color = MyGreenText,
                            style = TextStyle(
                                fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                                shadow = Shadow(
                                    color = MyPurpleShadow,
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
                    } //Button contents Row
                }
            }
        }
    }
}

