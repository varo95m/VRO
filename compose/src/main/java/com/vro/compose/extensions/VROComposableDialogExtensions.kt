package com.vro.compose.extensions

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vro.compose.dialog.VROComposableDialogContent
import com.vro.compose.initializers.InitializeEventsListener
import com.vro.core_android.lifecycleevent.createLifecycleEventObserver
import com.vro.core_android.viewmodel.VRODialogViewModel
import com.vro.dialog.VRODialogListener
import com.vro.event.VROEvent
import com.vro.state.VROState
import com.vro.state.VROStepper

@Composable
fun <VM : VRODialogViewModel<S, E>, S : VROState, E : VROEvent> VROComposableDialog(
    viewModel: @Composable () -> VM,
    content: VROComposableDialogContent<S, E>,
    listener: VRODialogListener? = null,
    onDismiss: () -> Unit,
) {
    VroComposableDialogContent(
        viewModel = viewModel(),
        content = content,
        onDismiss = onDismiss,
        listener = listener
    )
}

@Composable
private fun <VM : VRODialogViewModel<S, E>, S : VROState, E : VROEvent> VroComposableDialogContent(
    viewModel: VM,
    content: VROComposableDialogContent<S, E>,
    listener: VRODialogListener?,
    onDismiss: () -> Unit,
) {
    val screenLifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(screenLifecycle) {
        val observer = createLifecycleEventObserver(
            onStart = { viewModel.onStart() },
            onResume = { viewModel.onResume() }
        )
        screenLifecycle.addObserver(observer)
        onDispose {
            screenLifecycle.removeObserver(observer)
        }
    }
    val stepper = viewModel.stepper.collectAsStateWithLifecycle(
        initialValue = content.skeleton?.let {
            VROStepper.VROSkeletonStep(viewModel.initialState)
        } ?: VROStepper.VROStateStep(viewModel.initialState),
        lifecycle = screenLifecycle
    ).value

    if (stepper is VROStepper.VROSkeletonStep) {
        content.ComposableDialogSkeleton()
    } else {
        content.CreateDialog(stepper.state, viewModel, listener, onDismiss)
        (stepper as? VROStepper.VRODialogStep)?.let {
            content.onDialog(it.dialogState)
        }
        (stepper as? VROStepper.VROErrorStep)?.let {
            content.onError(it.error, it.data)
        }
    }
    InitializeEventsListener(viewModel)
}
