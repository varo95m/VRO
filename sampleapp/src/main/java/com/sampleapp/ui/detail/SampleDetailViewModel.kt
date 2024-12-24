package com.sampleapp.ui.detail

import com.sampleapp.ui.base.SampleBaseViewModel
import com.sampleapp.ui.detail.SampleDetailEvents.Profile
import com.sampleapp.ui.main.SampleDestinations
import com.vro.navstarter.VRONavStarter

class SampleDetailViewModel : SampleBaseViewModel<SampleDetailState, SampleDestinations, SampleDetailEvents>() {

    override val initialState: SampleDetailState = SampleDetailState.INITIAL

    override fun onStarter(starter: VRONavStarter?) {
        when (starter) {
            is SampleDetailStarter.Initial -> updateScreen { starter.state }
        }
    }

    override fun onEvent(event: SampleDetailEvents) {
        when (event) {
            Profile -> onActionProfileNavigationClick()
        }
    }

    private fun onActionProfileNavigationClick() {
        navigate(SampleDestinations.ProfileNavigation)
    }
}