package com.sampleapp.ui.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sampleapp.R
import com.sampleapp.dialog.bottomsheet.SampleBottomSheet
import com.sampleapp.dialog.bottomsheet.SampleBottomSheetViewModel
import com.sampleapp.dialog.withviewmodel.*
import com.sampleapp.topbar.sampleBackToolbar
import com.sampleapp.ui.base.SampleBaseScreen
import com.sampleapp.ui.home.SampleHomeViewModel.Companion.BOTTOM_SHEET
import com.sampleapp.ui.home.SampleHomeViewModel.Companion.ONE_TIME_LAUNCH
import com.sampleapp.ui.home.SampleHomeViewModel.Companion.SIMPLE_VIEW_MODEL_DIALOG
import com.vro.compose.extensions.VROComposableDialog
import com.vro.compose.extensions.VroBottomSheet
import com.vro.compose.preview.VROLightMultiDevicePreview
import com.vro.compose.skeleton.VROSkeleton
import com.vro.compose.states.VROBottomBarBaseState
import com.vro.compose.states.VROBottomBarBaseState.VROBottomBarState
import com.vro.compose.states.VROTopBarBaseState
import com.vro.compose.utils.vroVerticalScroll
import com.vro.constants.INT_ZERO
import com.vro.state.VRODialogData
import org.koin.androidx.compose.koinViewModel

class SampleHomeScreen(
    override val skeleton: VROSkeleton = SampleHomeSkeleton(),
) : SampleBaseScreen<SampleHomeState, SampleHomeEvents>() {


    override fun setTopBar(currentState: VROTopBarBaseState) = sampleBackToolbar(
        title = context.getString(R.string.home_toolbar),
        onNavigation = { navigateBack() }
    )

    override fun setBottomBar(currentState: VROBottomBarBaseState) =
        VROBottomBarState(selectedItem = INT_ZERO)

    @Composable
    override fun ScreenContent(state: SampleHomeState) {
        Column(
            modifier = Modifier
                .vroVerticalScroll()
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { event(SampleHomeEvents.UpdateText) }
            ) {
                Text(text = state.text)
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { event(SampleHomeEvents.ShowNavigationBottomSheet) }
            ) {
                Text(text = "Show Navigation Bottom Sheet")
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { event(SampleHomeEvents.ShowBottomSheet) }
            ) {
                Text(text = "Show Bottom Sheet")
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { event(SampleHomeEvents.ShowSimpleDialog) }
            ) {
                Text(text = "Show Dialog Example")
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { event(SampleHomeEvents.ShowSimpleDialogWithViewModel) }
            ) {
                Text(text = "Show Dialog VM Example")
            }
            OutlinedButton(
                modifier = Modifier.padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                onClick = { event((SampleHomeEvents.Profile)) }
            ) {
                Text(text = "Profile Navigation")
            }
            LastButtons()
        }
    }

    @Composable
    private fun LastButtons() {
        OutlinedButton(
            modifier = Modifier.padding(top = 16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
            onClick = { event(SampleHomeEvents.Detail) }
        ) {
            Text(text = "Detail Navigation")
        }
        OutlinedButton(
            modifier = Modifier.padding(top = 16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
            onClick = { event(SampleHomeEvents.ActivityFragment) }
        ) {
            Text(text = "Activity Fragment")
        }
        OutlinedButton(
            modifier = Modifier.padding(top = 16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
            onClick = { event(SampleHomeEvents.OneTimeLaunch) }
        ) {
            Text(text = "One Time Launch")
        }
    }

    override fun oneTimeHandler(id: Int, state: SampleHomeState) {
        when (id) {
            ONE_TIME_LAUNCH -> Toast.makeText(context, "1000", Toast.LENGTH_LONG).show()
        }
    }

    @Composable
    @VROLightMultiDevicePreview
    override fun ScreenPreview() {
        ScreenContent(state = SampleHomeState.INITIAL)
    }

    @Composable
    override fun onDialog(data: VRODialogData) {
        when (data.type) {
            SIMPLE_VIEW_MODEL_DIALOG -> VROComposableDialog(
                viewModel = { koinViewModel<SampleVMDialogViewModel>() },
                content = SampleVMDialog(),
                onDismiss = { event(SampleHomeEvents.VmDialogDismiss) },
                listener = object : SampleVMDialogListener {
                    override fun hideDialog() = event(SampleHomeEvents.VmDialogDismiss)
                }
            )

            BOTTOM_SHEET -> VroBottomSheet(
                viewModel = { koinViewModel<SampleBottomSheetViewModel>() },
                content = SampleBottomSheet(),
                onDismiss = { event(SampleHomeEvents.BottomSheetDismiss) },
                fullExpanded = true
            )

            else -> super.onDialog(data)
        }
    }
}