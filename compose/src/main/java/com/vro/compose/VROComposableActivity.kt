package com.vro.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.*
import com.vro.compose.components.VroTopBar
import com.vro.compose.extensions.destinationRoute
import com.vro.compose.screen.VROScreen
import com.vro.compose.snackbar.VROSnackbar
import com.vro.compose.states.VROBottomBarBaseState
import com.vro.compose.states.VROBottomBarBaseState.VROBottomBarStartState
import com.vro.compose.states.VROBottomBarBaseState.VROBottomBarState
import com.vro.compose.states.VROSnackBarState
import com.vro.compose.states.VROTopBarBaseState
import com.vro.compose.states.VROTopBarBaseState.VROTopBarStartState
import com.vro.navigation.putStarterParam
import com.vro.navstarter.VRONavStarter

abstract class VROComposableActivity : ComponentActivity() {

    open val theme: VROComposableTheme? = null

    abstract val startScreen: VROScreen<*, *>

    private lateinit var navController: NavController

    @Composable
    private fun CreateTheme(
        lightColors: ColorScheme,
        darkColors: ColorScheme,
        typography: Typography,
        content: @Composable () -> Unit,
    ) {
        MaterialTheme(
            colorScheme = if (isSystemInDarkTheme()) darkColors else lightColors,
            typography = typography
        ) {
            CompositionLocalProvider(
                content = content
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            theme?.also {
                CreateTheme(it.lightColors, it.darkColors, it.typography) { Initialize(MaterialTheme.colorScheme.background) }
            } ?: run { Initialize() }
        }
    }

    @OptIn(ExperimentalMaterialNavigationApi::class)
    @Composable
    fun Initialize(backgroundColor: Color? = null) {
        val bottomSheetNavigator = rememberBottomSheetNavigator()
        val navController = rememberNavController(bottomSheetNavigator)
        this.navController = navController
        val topBarState = remember { mutableStateOf<VROTopBarBaseState>(VROTopBarStartState()) }
        val bottomBarState = remember { mutableStateOf<VROBottomBarBaseState>(VROBottomBarStartState()) }
        val snackbarHostState = remember { SnackbarHostState() }
        val snackbarState = remember { mutableStateOf<VROSnackBarState>(VROSnackBarState(snackbarHostState)) }
        ModalBottomSheetLayout(
            modifier = Modifier.fillMaxSize(),
            bottomSheetNavigator = bottomSheetNavigator,
            sheetShape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp),
        ) {
            Scaffold(
                containerColor = backgroundColor ?: Color.Transparent,
                topBar = {
                    (topBarState.value as? VROTopBarBaseState.VROTopBarState)?.let {
                        if (topBarState.value.visibility) {
                            VroTopBar(state = it)
                        }
                    }
                },
                bottomBar = {
                    (bottomBarState.value as? VROBottomBarState)?.let {
                        if (bottomBarState.value.visibility) {
                            BottomBar(selectedItem = (it.selectedItem))
                        }
                    }
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = {
                            VROSnackbar(
                                data = it,
                                backgroundColor = snackbarState.value.backgroundColor,
                                textColor = snackbarState.value.textColor,
                                actionButtonColor = snackbarState.value.actionColor
                            )
                        }
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = if (!bottomBarState.value.visibility) 0.dp
                            else innerPadding.calculateBottomPadding()
                        )
                        .fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = startScreen.destinationRoute()
                    ) {
                        createComposableContent(
                            navController = navController,
                            topBarState = topBarState,
                            bottomBarState = bottomBarState,
                            snackbarState = snackbarState
                        )
                    }
                }
            }
        }
    }

    @Composable
    open fun BottomBar(selectedItem: Int) = Unit

    fun navigateToScreen(
        screen: VROScreen<*, *>,
        starter: VRONavStarter? = null,
    ) {
        navController.navigate(screen.destinationRoute()) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
        starter?.let {
            putStarterParam(navController.currentDestination?.id.toString(), it)
        }
    }

    abstract fun NavGraphBuilder.createComposableContent(
        navController: NavHostController,
        topBarState: MutableState<VROTopBarBaseState>,
        bottomBarState: MutableState<VROBottomBarBaseState>,
        snackbarState: MutableState<VROSnackBarState>,
    )
}