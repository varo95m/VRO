package com.vro.state

import com.vro.constants.INT_ONE
import com.vro.constants.INT_ZERO
import com.vro.event.VROEvent
import com.vro.navigation.VRODestination
import com.vro.navigation.VRONavigationState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun <D : VRODestination> createNavigationSharedFlow() = MutableSharedFlow<VRONavigationState<D>?>(
    replay = INT_ONE,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

fun <S> createStepperSharedFlow() = MutableSharedFlow<VROStepper<S>>(
    replay = INT_ONE,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

fun <S> createOneTimeSharedFlow() = MutableSharedFlow<VROOneTimeState<S>>(
    replay = INT_ONE,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

fun <E> createEventSharedFlow() = MutableSharedFlow<E>(
    replay = INT_ZERO,
    extraBufferCapacity = INT_ONE,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)