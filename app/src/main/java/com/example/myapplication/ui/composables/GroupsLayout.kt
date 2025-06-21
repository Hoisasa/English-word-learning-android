package com.example.myapplication.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview


//val subgroupsList = mutableStateListOf<String>()

//@Composable
//fun grouping(
//    targetList: SnapshotStateList<String>,
//    currentList: SnapshotStateList<String>,
//    currentGroup: MutableState<String>,
//    screenState: MutableState<String>
//) {


//}


//@Composable
//fun GroupsScreen (
//
//    onBack: () -> Unit,
//    onItemSelect: (String) -> Unit,
//    names: SnapshotStateList<String>,
//    modifier: Modifier = Modifier,
//) {
//    BackHandler {
//        onBack()
//    }
//}
//
//@Composable
//fun SubGroupsScreen (
//    onBack: () -> Unit,
//    onSubGroupSelect: (String) -> Unit,
//    names: SnapshotStateList<String>,
//    modifier: Modifier = Modifier,
//) {
//       Display_groups(onSubGroupSelect, names, modifier)
//    BackHandler {
//        onBack()
//    }
//}

@Preview
@Composable
fun Display_groups(buttonFunction: (String) -> Unit = {},
                   groupNames: SnapshotStateList<String> = mutableStateListOf("A", "B", "C", "D"),
                   modifier: Modifier = Modifier
) {
    
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
    ) {
        items(groupNames) { name ->
            Button(
                onClick = {
                            buttonFunction(name)
                          },
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1AE96A),
                    contentColor = Color(0xFF555555),
                ),
            ) {
                Text(name)
            }
        }
    }
}


