package com.sharksempire.englishcards.components.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharksempire.englishcards.ui.composables.GroupsViewState
import com.sharksempire.englishcards.ui.theme.MyGreen
import com.sharksempire.englishcards.ui.theme.MyPurple

@Composable
fun GroupsFilterItem(
    filters: GroupsViewState.Success.FilterState,
    onFilterCLicked: (String) -> Unit,
    modifier: Modifier = Modifier)
{
    
    Row(modifier.padding(bottom = 20.dp),
        Arrangement.SpaceAround
    ){
        filters.allFilters.forEach { pos ->
            val filterColor = if (pos in filters.selectedFilters) MyGreen else MyPurple
            Text(
                pos,
                color = filterColor,
                modifier = Modifier
                    .padding(6.dp)
                    .clickable {
                        onFilterCLicked(pos)
                    },
                fontSize = 32.sp
            )
        }
    }
}

@Preview(widthDp = 800)
@Composable
fun PreviewFilters() {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item{
            GroupsFilterItem(
                GroupsViewState.Success.FilterState(
                    listOf("Verb", "Noun", "Adjective", "Preposition"),
                    listOf("Verb", "Adjective", "Preposition"),
                    
                ),
                {}
            )
        }
    }
}