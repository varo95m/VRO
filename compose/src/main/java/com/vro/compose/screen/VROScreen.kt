package com.vro.compose.screen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vro.compose.states.VROBottomBarBaseState
import com.vro.compose.states.VROBottomBarBaseState.VROBottomBarState
import com.vro.compose.states.VROTopBarBaseState
import com.vro.compose.states.VROTopBarBaseState.VROTopBarState
import com.vro.event.VROEvent
import com.vro.state.VROState

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
}
