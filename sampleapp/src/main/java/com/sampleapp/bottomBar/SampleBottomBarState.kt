package com.sampleapp.bottomBar

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vro.compose.states.VROComposableScaffoldState.VROBottomBarState

class SampleBottomBarState(
    val items: List<VROBottomBarItem>,
) : VROBottomBarState(
    background = Color.Transparent,
    height = 56.dp,
    itemList = items
)