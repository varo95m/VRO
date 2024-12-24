package com.vro.core_android.viewmodel

import androidx.lifecycle.ViewModel
import com.vro.coroutine.VROBaseConcurrencyManager
import com.vro.coroutine.VROConcurrencyManager
import com.vro.event.*
import com.vro.navigation.*
import com.vro.navstarter.VRONavStarter
import com.vro.state.*
import com.vro.state.VROStepper.VRODialogStep
import com.vro.state.VROStepper.VROErrorStep
import com.vro.state.VROStepper.VROStateStep
import com.vro.usecase.MainUseCaseResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

abstract class VROViewModelCore<S : VROState, E : VROEvent> : ViewModel(), VROEventLauncher<E> {

    abstract val initialState: S

    protected var screenState: S by VroStateDelegate { initialState }

    protected val observableStepper = createStepperSharedFlow<S>()

    private val observableOneTime = createOneTimeSharedFlow<S>()

    val stepper: Flow<VROStepper<S>> = observableStepper

    fun getOneTimeEvents(): Flow<VROOneTimeState<S>> = observableOneTime

    var concurrencyManager: VROBaseConcurrencyManager = VROConcurrencyManager()

    override val eventObservable: MutableSharedFlow<E> = createEventSharedFlow()

    open fun onStarter(starter: VRONavStarter?) = Unit

    open fun onStart() = Unit

    open fun onNavResult(result: VROBackResult) = Unit

    abstract fun onEvent(event: E)

    fun updateScreen(changeStateFunction: S.() -> S) {
        screenState = changeStateFunction.invoke(screenState)
        observableStepper.tryEmit(VROStateStep(screenState))
    }

    fun updateScreen() {
        observableStepper.tryEmit(VROStateStep(screenState))
    }

    fun updateState(changeStateFunction: S.() -> S) {
        screenState = changeStateFunction.invoke(screenState)
    }

    fun updateDialog(dialogState: VRODialogData, clearScreen: Boolean = true) {
        if (clearScreen) updateScreen { screenState }
        observableStepper.tryEmit(VRODialogStep(screenState, dialogState))
    }

    fun updateError(error: Throwable, data: Any? = null) {
        observableStepper.tryEmit(VROErrorStep(screenState, error, data))
    }

    fun updateOneTime(id: Int, state: S) {
        observableOneTime.tryEmit(VROOneTimeState.Launch(state = state, id = id))
    }

    fun clearOneTime() {
        observableOneTime.tryEmit(VROOneTimeState.Clear())
    }

    open fun onPause() = Unit

    open fun onResume() {
        updateState { screenState }
    }

    fun checkDataState(): S = screenState

    fun <T> executeCoroutine(
        fullException: Boolean = false,
        action: suspend CoroutineScope.() -> T,
    ): MainUseCaseResult<T> {
        return MainUseCaseResult(concurrencyManager, fullException, action)
    }
}