package com.vro.compose.states

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color

data class VROSnackBarState(
    val hostState: SnackbarHostState,
    val textColor: Color = Color.Black,
    val backgroundColor: Color = Color.Black,
    val actionColor: Color = Color.Black,
)