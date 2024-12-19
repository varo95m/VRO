package com.sampleapp.ui.main

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.sampleapp.bottombar.SampleBottombar
import com.sampleapp.dialog.bottomsheet.SampleBottomSheet
import com.sampleapp.dialog.bottomsheet.SampleBottomSheetNavViewModel
import com.sampleapp.dialog.bottomsheet.SampleBottomSheetNavigator
import com.sampleapp.styles.SampleTheme
import com.sampleapp.ui.detail.*
import com.sampleapp.ui.home.*
import com.sampleapp.ui.profile.*
import com.vro.compose.VROComposableActivity
import com.vro.compose.VROComposableTheme
import com.vro.compose.extensions.vroBottomSheet
import com.vro.compose.extensions.vroComposableScreen
import com.vro.compose.states.VROBottomBarBaseState
import com.vro.compose.states.VROSnackBarState
import com.vro.compose.states.VROTopBarBaseState
import org.koin.androidx.compose.koinViewModel

class SampleMainActivity : VROComposableActivity() {

    override val startScreen = SampleHomeScreen()

    override val theme: VROComposableTheme = SampleTheme

    @Composable
    override fun BottomBar(selectedItem: Int) {
        SampleBottombar(
            onHomeClick = { navigateToScreen(SampleHomeScreen()) },
            onProfileClick = { navigateToScreen(SampleProfileScreen()) },
            selectedItem = selectedItem
        )
    }

    override fun NavGraphBuilder.createComposableContent(
        navController: NavHostController,
        topBarState: MutableState<VROTopBarBaseState>,
        bottomBarState: MutableState<VROBottomBarBaseState>,
        snackbarState: MutableState<VROSnackBarState>,
    ) {
        vroComposableScreen(
            viewModel = { koinViewModel<SampleHomeViewModel>() },
            navigator = SampleHomeNavigator(this@SampleMainActivity, navController),
            content = SampleHomeScreen(),
            topBarState = topBarState,
            bottomBarState = bottomBarState,
            snackbarState = snackbarState
        )
        vroComposableScreen(
            viewModel = { koinViewModel<SampleProfileViewModel>() },
            navigator = SampleProfileNavigator(this@SampleMainActivity, navController),
            content = SampleProfileScreen(),
            topBarState = topBarState,
            bottomBarState = bottomBarState,
            snackbarState = snackbarState
        )
        vroComposableScreen(
            viewModel = { koinViewModel<SampleDetailViewModel>() },
            navigator = SampleDetailNavigator(this@SampleMainActivity, navController),
            content = SampleDetailScreen(),
            topBarState = topBarState,
            bottomBarState = bottomBarState,
            snackbarState = snackbarState
        )
        vroBottomSheet(
            content = SampleBottomSheet(),
            viewModel = { koinViewModel<SampleBottomSheetNavViewModel>() },
            navigator = SampleBottomSheetNavigator(this@SampleMainActivity, navController)
        )
    }
}