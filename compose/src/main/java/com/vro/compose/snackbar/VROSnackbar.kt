package com.vro.compose.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VROSnackbar(
    data: SnackbarData,
    backgroundColor: Color = Color(0xFFFFFFFF),
    textColor: Color,
    actionButtonColor: Color,
) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        containerColor = backgroundColor,
        action = {
            data.visuals.actionLabel?.let { actionLabel ->
                TextButton(onClick = { data.performAction() }) {
                    Text(
                        text = actionLabel,
                        color = actionButtonColor
                    )
                }
            }
        }
    ) {
        Text(
            text = data.visuals.message,
            color = textColor
        )
    }
}