package com.sampleapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sampleapp.R
import com.sampleapp.topbar.sampleBackToolbar
import com.sampleapp.ui.base.SampleBaseScreen
import com.vro.compose.preview.VROLightMultiDevicePreview
import com.vro.compose.states.VROBottomBarState
import com.vro.constants.INT_ONE

class SampleProfileScreen : SampleBaseScreen<SampleProfileState, SampleProfileEvents>() {

    override fun setBottomBar() = VROBottomBarState(selectedItem = INT_ONE)

    override fun setTopBar() = sampleBackToolbar(
        title = context.getString(R.string.profile_toolbar),
        onNavigation = { navigateBack() }
    )

    @Composable
    override fun ScreenContent(state: SampleProfileState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 32.dp)
        ) {
            Text(text = "This is the user profile")
        }
    }

    @VROLightMultiDevicePreview
    @Composable
    override fun ScreenPreview() {
        ScreenContent(state = SampleProfileState.INITIAL)
    }
}