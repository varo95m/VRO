package com.vro.compose.initializers

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vro.compose.VROComposableViewModel
import com.vro.compose.screen.VROScreen
import com.vro.compose.states.*
import com.vro.core_android.lifecycleevent.createLifecycleEventObserver
import com.vro.core_android.navigation.VRONavigator
import com.vro.core_android.viewmodel.VROViewModelCore
import com.vro.event.VROEvent
import com.vro.navigation.*
import com.vro.state.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <VM : VROComposableViewModel<S, D, E>, S : VROState, D : VRODestination, E : VROEvent> InitializeNavigatorListener(
    viewModel: VM,
    navigator: VRONavigator<D>,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.getNavigationState().collect {
            it?.destination?.let { destination ->
                if (!destination.isNavigated) {
                    navigator.navigate(destination)
                    destination.setNavigated()
                }
            } ?: navigator.navigateBack(it?.backResult)
        }
    }
}

@Composable
fun <VM : VROComposableViewModel<S, D, E>, S : VROState, D : VRODestination, E : VROEvent> InitializeOneTimeListener(
    viewModel: VM,
    content: VROScreen<S, E>,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.getOneTimeEvents().collectLatest { oneTime ->
            if (oneTime is VROOneTimeState.Launch) {
                content.oneTimeHandler(oneTime.id, oneTime.state)
                viewModel.clearOneTime()
            }
        }
    }
}

@Composable
fun <VM : VROComposableViewModel<S, D, E>, S : VROState, D : VRODestination, E : VROEvent> InitializeStepperListener(
    viewModel: VM,
    content: VROScreen<S, E>,
    screenLifecycle: Lifecycle,
    topBarState: MutableState<VROTopBarBaseState>,
    bottomBarState: MutableState<VROBottomBarBaseState>,
    snackbarState: MutableState<VROSnackBarState>,
) {
    val stepper = viewModel.stepper.collectAsStateWithLifecycle(
        initialValue =
        content.skeleton?.let {
            VROStepper.VROSkeletonStep(viewModel.initialState)
        } ?: VROStepper.VROStateStep(viewModel.initialState),
        lifecycle = screenLifecycle
    ).value

    if (stepper is VROStepper.VROSkeletonStep) {
        content.ComposableScreenSkeleton()
    } else {
        content.ComposableScreenContainer(
            state = stepper.state,
            topBarState = topBarState,
            bottomBarState = bottomBarState,
            snackbarState = snackbarState
        )
        (stepper as? VROStepper.VRODialogStep)?.let {
            content.onDialog(it.dialogState)
        }
        (stepper as? VROStepper.VROErrorStep)?.let {
            content.onError(it.error, it.data)
        }
    }
}

@Composable
fun <VM : VROComposableViewModel<S, D, E>, S : VROState, D : VRODestination, E : VROEvent> InitializeLifecycleObserver(
    viewModel: VM,
    content: VROScreen<S, E>,
    screenLifecycle: Lifecycle,
    topBarState: MutableState<VROTopBarBaseState>,
    bottomBarState: MutableState<VROBottomBarBaseState>,
    navController: NavController,
) {
    DisposableEffect(screenLifecycle) {
        val observer = createLifecycleEventObserver(
            onCreate = {
                content.configureScaffold(topBarState, bottomBarState)
                viewModel.onStarter(getStarterParam(navController.currentDestination?.id.toString()))
            },
            onStart = {
                viewModel.onStart()
            },
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
            viewModel.onPause()
            screenLifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun <VM : VROViewModelCore<S, E>, S : VROState, E : VROEvent> InitializeEventsListener(
    viewModel: VM,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.eventObservable.collect {
            viewModel.onEvent(it)
        }
    }
}