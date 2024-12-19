package com.vro.compose.screen

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vro.compose.states.VROBottomBarBaseState
import com.vro.compose.states.VROBottomBarBaseState.VROBottomBarState
import com.vro.compose.states.VROSnackBarState
import com.vro.compose.states.VROTopBarBaseState
import com.vro.compose.states.VROTopBarBaseState.VROTopBarState
import com.vro.constants.EMPTY_STRING
import com.vro.event.VROEvent
import com.vro.state.VROState
import kotlinx.coroutines.launch

abstract class VROScreen<S : VROState, E : VROEvent> : VROScreenBase<S, E>() {

    lateinit var context: Context

    internal fun configureScaffold(
        topBarState: MutableState<VROTopBarBaseState>,
        bottomBarState: MutableState<VROBottomBarBaseState>,
    ) {
        topBarState.value = setTopBar(topBarState.value)
        bottomBarState.value = setBottomBar(bottomBarState.value)
    }

    @Composable
    fun UpdateTopBar(changeStateFunction: VROTopBarState.() -> VROTopBarState) {
        val navController = rememberNavController()
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
        LaunchedEffect(currentDestination) {
            (topBarState.value as? VROTopBarState)?.let {
                topBarState.value = changeStateFunction.invoke(it)
            }
        }
    }

    @Composable
    fun UpdateBottomBar(changeStateFunction: VROBottomBarState.() -> VROBottomBarState) {
        val navController = rememberNavController()
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
        LaunchedEffect(currentDestination) {
            (bottomBarState.value as? VROBottomBarState)?.let {
                bottomBarState.value = changeStateFunction.invoke(it)
            }
        }
    }

    fun showSnackbar(
        message: String,
        actionLabel: String = EMPTY_STRING,
        duration: SnackbarDuration = SnackbarDuration.Short,
        textColor: Color = Color(0xFF1C1B1F),
        backgroundColor: Color = Color(0xFFFFFFFF),
        actionColor: Color = Color(0xFF6750A4),
        onAction: () -> Unit = {},
        onDismiss: () -> Unit = {},
    ) {
        coroutineScope.launch {
            snackbarState.value = VROSnackBarState(
                snackbarState.value.hostState,
                textColor,
                backgroundColor,
                actionColor
            )
            val snackResult = snackbarState.value.hostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration,
            )
            when (snackResult) {
                SnackbarResult.ActionPerformed -> onAction()
                SnackbarResult.Dismissed -> onDismiss()
            }
        }
    }
}
