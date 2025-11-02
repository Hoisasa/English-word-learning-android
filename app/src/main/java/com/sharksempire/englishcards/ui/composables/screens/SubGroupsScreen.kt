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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.sharksempire.englishcards.components.groups.ScrollableTextWithArrow
import com.sharksempire.englishcards.ui.composables.GroupsWithProgressData
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyGreenText
import com.sharksempire.englishcards.ui.theme.MyPurple
import com.sharksempire.englishcards.ui.theme.MyPurpleShadow
import com.sharksempire.englishcards.ui.theme.groupsStyle
import com.sharksempire.englishcards.viewmodels.SubGroupsViewModel

sealed interface SubGroupsViewState {
    object Loading : SubGroupsViewState
    data class Error(val message: String) : SubGroupsViewState
    data class Success(val content: List<GroupsWithProgressData>) : SubGroupsViewState
}
@Composable
fun Display_subgroups(
    onSubgroupSelected: (String) -> Unit,
    viewModel: SubGroupsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    
    when(val viewState = state) {
        SubGroupsViewState.Loading -> CircularProgressIndicator(modifier = Modifier.size(50.dp))
        is SubGroupsViewState.Error -> Text(text = viewState.message, fontSize = 20.sp)
        is SubGroupsViewState.Success -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    items(
                        items = viewState.content,
                        key = { it.name }
                    ) { group ->
                        Button(
                            onClick = {
                                onSubgroupSelected(group.name)
                            },
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
                            }
                        }
                    }
                }
            }
        }
    }
}

