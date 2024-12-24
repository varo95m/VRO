package com.sampleapp.ui.samplefragment.destination

import com.sampleapp.ui.samplefragment.destination.SampleComposableDestinationFragmentEvents.BackWithResult
import com.sampleapp.ui.samplefragment.destination.SampleComposableDestinationFragmentEvents.Update
import com.sampleapp.ui.samplefragment.destination.SampleComposableDestinationFragmentNavigator.SampleComposableDestinationFragmentDestinations
import com.vro.compose.VROComposableViewModel
import com.vro.navigation.VROBackResult
import com.vro.navstarter.VRONavStarter

class SampleComposableDestinationFragmentViewModel :
    VROComposableViewModel<SampleComposableDestinationFragmentState, SampleComposableDestinationFragmentDestinations, SampleComposableDestinationFragmentEvents>() {

    override val initialState: SampleComposableDestinationFragmentState = SampleComposableDestinationFragmentState.INITIAL

    override fun onStarter(starter: VRONavStarter?) {
        when (starter) {
            is SampleComposableDestinationStarter.Initial -> updateScreen { starter.state }
        }
    }

    override fun onEvent(event: SampleComposableDestinationFragmentEvents) {
        when (event) {
            BackWithResult -> doBack(VROBackResult(100))
            Update -> Unit
        }
    }
}