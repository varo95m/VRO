package com.vro.dialog

import androidx.lifecycle.ViewModel
import com.vro.coroutine.VROBaseConcurrencyManager
import com.vro.coroutine.VROConcurrencyManager
import com.vro.event.VROEvent
import com.vro.event.VROEventListener
import com.vro.navigation.VROBackResult
import com.vro.state.*
import com.vro.usecase.MainUseCaseResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class VRODialogViewModel<S : VROState, E : VROEvent> : ViewModel(), VROEventListener<E> {

    abstract val initialState: S

    private var screenState: S by VroStateDelegate { initialState }

    private val observableStepper = createStepperSharedFlow<S>()

    internal val stepper: SharedFlow<VROStepper<S>> = observableStepper

    private var concurrencyManager: VROBaseConcurrencyManager = VROConcurrencyManager()

    fun checkDataState(): S = screenState

    open fun onStart() = Unit

    internal fun setInitialState(state: S) {
        screenState = state
    }

    fun updateScreen(changeStateFunction: S.() -> S) {
        screenState = changeStateFunction.invoke(screenState)
        observableStepper.tryEmit(VROStepper.VROStateStep(screenState))
    }

    fun updateScreen() {
        observableStepper.tryEmit(VROStepper.VROStateStep(screenState))
    }

    fun updateState(changeStateFunction: S.() -> S) {
        screenState = changeStateFunction.invoke(screenState)
    }

    open fun onResume() {
        updateState { screenState }
    }

    fun <T> executeCoroutine(
        fullException: Boolean = false,
        action: suspend CoroutineScope.() -> T,
    ): MainUseCaseResult<T> {
        return MainUseCaseResult(concurrencyManager, fullException, action)
    }

    override fun eventBack(result: VROBackResult?) = Unit
}