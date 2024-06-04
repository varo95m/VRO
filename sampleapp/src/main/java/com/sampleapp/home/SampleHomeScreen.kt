package com.sampleapp.home

import androidx.compose.runtime.Composable
import com.sampleapp.base.SampleBaseScreen
import com.sampleapp.dialog.bottomsheet.SampleBottomSheet
import com.sampleapp.dialog.bottomsheet.SampleBottomSheetViewModel
import com.sampleapp.dialog.withviewmodel.SampleVMDialog
import com.sampleapp.dialog.withviewmodel.SampleVMDialogViewModel
import com.sampleapp.home.SampleHomeViewModel.Companion.BOTTOM_SHEET
import com.sampleapp.home.SampleHomeViewModel.Companion.SIMPLE_VIEW_MODEL_DIALOG
import com.sampleapp.home.sections.BottomButtonSection
import com.sampleapp.home.sections.TopButtonSection
import com.sampleapp.topbar.sampleHomeToolbar
import com.vro.compose.extensions.VROComposableDialog
import com.vro.compose.extensions.VroBottomSheet
import com.vro.compose.preview.VROLightMultiDevicePreview
import com.vro.state.VRODialogState
import org.koin.androidx.compose.koinViewModel

class SampleHomeScreen : SampleBaseScreen<SampleHomeState, SampleHomeEvents>() {

    override fun setTopBar() =
        sampleHomeToolbar(
            context = context,
            onAction = { event(SampleHomeEvents.ProfileNavigation) }
        )

    @Composable
    override fun composableContent() =
        listOf(
            TopButtonSection(),
            BottomButtonSection()
        )

    @Composable
    override fun composableTabletContent() =
        listOf(
            TopButtonSection(),
            BottomButtonSection()
        )

    @Composable
    @VROLightMultiDevicePreview
    override fun ComposablePreview() {
        CreatePreview()
    }

    @Composable
    override fun OnDialog(data: VRODialogState) {
        when (data.type) {
            SIMPLE_VIEW_MODEL_DIALOG -> VROComposableDialog(
                viewModel = { koinViewModel<SampleVMDialogViewModel>() },
                content = SampleVMDialog(),
                onDismiss = { event(SampleHomeEvents.VmDialogDismiss) },
            )

            BOTTOM_SHEET -> VroBottomSheet(
                viewModel = { koinViewModel<SampleBottomSheetViewModel>() },
                content = SampleBottomSheet(),
                onDismiss = { event(SampleHomeEvents.BottomSheetDismiss) },
                fullExpanded = true
            )

            else -> super.OnDialog(data)
        }
    }
}