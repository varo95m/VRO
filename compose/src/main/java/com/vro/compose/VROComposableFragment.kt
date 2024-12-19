package com.vro.compose

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.accompanist.navigation.material.*
import com.vro.compose.components.VroTopBar
import com.vro.compose.extensions.VROComposableFragmentScreen
import com.vro.compose.screen.VROScreen
import com.vro.compose.states.VROBottomBarBaseState
import com.vro.compose.states.VROBottomBarBaseState.VROBottomBarStartState
import com.vro.compose.states.VROBottomBarBaseState.VROBottomBarState
import com.vro.compose.states.VROSnackBarState
import com.vro.compose.states.VROTopBarBaseState
import com.vro.compose.states.VROTopBarBaseState.VROTopBarStartState
import com.vro.core_android.fragment.VROFragmentInjection
import com.vro.core_android.navigation.VROFragmentNavigator
import com.vro.event.VROEvent
import com.vro.navigation.VROBackResult
import com.vro.navigation.VRODestination
import com.vro.state.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class VROComposableFragment<
        VM : VROComposableViewModel<S, D, E>,
        S : VROState,
        D : VRODestination,
        SC : VROScreen<S, E>,
        E : VROEvent,
        > : VROFragmentInjection<VM>() {

    abstract val navigator: VROFragmentNavigator<D>

    open val theme: VROComposableTheme? = null

    private lateinit var navController: NavController

    @Composable
    abstract fun composableView(): SC

    @Composable
    private fun CreateTheme(
        lightColors: ColorScheme,
        darkColors: ColorScheme,
        typography: Typography,
        content: @Composable () -> Unit,
    ) {
        MaterialTheme(
            colorScheme = if (isSystemInDarkTheme()) darkColors else lightColors,
            typography = typography,
        ) {
            CompositionLocalProvider(
                content = content
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers(viewModel, this)
    }

    private fun setObservers(viewModel: VM, fragment: Fragment) {
        fragment.lifecycleScope.launch {
            viewModel.stepper.collectLatest { stepper ->
                when (stepper) {
                    is VROStepper.VRODialogStep -> onLoadDialog(stepper.dialogState)
                    is VROStepper.VROErrorStep -> onError(stepper.error)
                    else -> Unit
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))
            setContent {
                theme?.also {
                    CreateTheme(it.lightColors, it.darkColors, it.typography) {
                        Initialize(backgroundColor = MaterialTheme.colorScheme.background)
                    }
                } ?: run {
                    Initialize()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialNavigationApi::class)
    @Composable
    fun Initialize(
        backgroundColor: Color? = null,
    ) {
        val bottomSheetNavigator = rememberBottomSheetNavigator()
        this.navController = this@VROComposableFragment.findNavController()
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
                }
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
                    VROComposableFragmentScreen(
                        viewModel = viewModel,
                        navigator = navigator,
                        topBarState = topBarState,
                        bottomBarState = bottomBarState,
                        snackbarState = snackbarState,
                        content = composableView()
                    )
                }
            }
        }
    }

    open fun onLoadDialog(data: VRODialogData) = Unit

    open fun onError(error: Throwable) = Unit

    @Composable
    open fun BottomBar(selectedItem: Int) = Unit

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    fun event(event: E) {
        viewModel.doEvent(event)
    }

    fun navigateBack(result: VROBackResult?) {
        viewModel.doBack(result)
    }
}