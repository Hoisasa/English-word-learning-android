package com.example.myapplication.ui.composables.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.composables.Display_groups

@Composable
fun GroupsScreen (
    onGroupSelect: (String) -> Unit,
    groupNames: SnapshotStateList<String>,
    modifier: Modifier = Modifier,
) {
    Display_groups(onGroupSelect, groupNames, modifier)
}