package com.vro.compose.initializers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.vro.compose.VROComposableViewModel
import com.vro.core_android.lifecycleevent.createLifecycleEventObserver
import com.vro.event.VROEvent
import com.vro.navigation.*
import com.vro.state.VROState

@Composable
fun <VM : VROComposableViewModel<S, D, E>, S : VROState, D : VRODestination, E : VROEvent> InitializeLifecycleObserver(
    viewModel: VM,
    screenLifecycle: Lifecycle,
    navController: NavController,
) {
    DisposableEffect(screenLifecycle) {
        val observer = createLifecycleEventObserver(
            onCreate = { viewModel.onStarter(getStarterParam(navController.currentDestination?.id.toString())) },
            onStart = { viewModel.onStart() },
            onResume = {
                getResultParam(navController.currentDestination?.id.toString())?.let {
                    viewModel.onNavResult(it)
                }
                viewModel.onResume()
            },
            onPause = { viewModel.onPause() }
        )
        screenLifecycle.addObserver(observer)
        onDispose {
            screenLifecycle.removeObserver(observer)
        }
    }
}