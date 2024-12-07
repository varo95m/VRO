package com.sampleapp.ui.samplefragment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sampleapp.R
import com.sampleapp.topbar.sampleBackToolbar
import com.sampleapp.ui.samplefragment.SampleComposableFragmentEvents.NavigateDestination
import com.sampleapp.ui.samplefragment.SampleComposableFragmentEvents.Update
import com.vro.compose.preview.VROLightMultiDevicePreview
import com.vro.compose.screen.VROScreen
import com.vro.compose.states.VROTopBarBaseState

class SampleComposableFragmentScreen :
    VROScreen<SampleComposableFragmentState, SampleComposableFragmentEvents>() {

    override fun setTopBar(currentState: VROTopBarBaseState) = sampleBackToolbar(
        title = context.getString(R.string.profile_toolbar),
        onNavigation = { navigateBack() }
    )

    @Composable
    override fun ScreenContent(state: SampleComposableFragmentState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { navigateBack() }
            ) {
                Text(text = "Navigate Back")
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { event(Update) }
            ) {
                Text(text = state.text)
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { event(NavigateDestination) }
            ) {
                Text(text = "Navigate To Composable Destination")
            }
        }
    }

    @VROLightMultiDevicePreview
    @Composable
    override fun ScreenPreview() {
        ScreenContent(state = SampleComposableFragmentState.INITIAL)
    }
}