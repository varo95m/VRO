package com.vro.compose

import androidx.navigation.NavController
import com.vro.compose.dialog.VROComposableBottomSheetContent
import com.vro.compose.dialog.VROComposableViewModelBottomSheetContent
import com.vro.compose.extensions.destinationRoute
import com.vro.compose.screen.VROScreen
import com.vro.core_android.navigation.VRONavigator
import com.vro.navigation.VRODestination
import com.vro.navigation.putStarterParam
import com.vro.navstarter.VRONavStarter

abstract class VROComposableNavigator<D : VRODestination>(
    override val activity: VROComposableActivity,
    override val navController: NavController,
) : VRONavigator<D> {

    fun navigateToScreen(
        screen: VROScreen<*, *>,
        starter: VRONavStarter? = null,
        popScreen: VROScreen<*, *>? = null,
        inclusive: Boolean = false,
    ) {
        navigateToRoute(
            destinationRoute = screen.destinationRoute(),
            starter = starter,
            popScreen = popScreen,
            inclusive = inclusive
        )
    }

    fun openBottomSheet(
        bottomSheet: VROComposableViewModelBottomSheetContent<*, *>,
        starter: VRONavStarter? = null,
        popScreen: VROScreen<*, *>? = null,
        inclusive: Boolean = false,
    ) {
        navigateToRoute(
            destinationRoute = bottomSheet.destinationRoute(),
            starter = starter,
            popScreen = popScreen,
            inclusive = inclusive
        )
    }

    fun openBottomSheet(
        bottomSheet: VROComposableBottomSheetContent<*>,
        starter: VRONavStarter? = null,
        popScreen: VROScreen<*, *>? = null,
        inclusive: Boolean = false,
    ) {
        navigateToRoute(
            destinationRoute = bottomSheet.destinationRoute(),
            starter = starter,
            popScreen = popScreen,
            inclusive = inclusive
        )
    }

    private fun navigateToRoute(
        destinationRoute: String,
        starter: VRONavStarter? = null,
        popScreen: VROScreen<*, *>? = null,
        inclusive: Boolean = false,
    ) {
        navController.navigate(destinationRoute) {
            popScreen?.destinationRoute()?.let {
                popUpTo(it) {
                    this.inclusive = inclusive
                }
            }
        }
        starter?.let {
            putStarterParam(navController.currentDestination?.id.toString(), it)
        }
    }
}