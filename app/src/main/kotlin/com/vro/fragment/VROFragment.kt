package com.vro.fragment

import android.os.Bundle
import android.view.*
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.vro.core_android.fragment.VROFragmentInjection
import com.vro.core_android.lifecycleevent.createLifecycleEventObserver
import com.vro.core_android.navigation.VRONavigator.Companion.NAVIGATION_STATE
import com.vro.event.VROEvent
import com.vro.navigation.*
import com.vro.state.*
import com.vro.state.VROStepper.VROStateStep
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class VROFragment<
        VM : VROViewModel<S, D, E>,
        S : VROState,
        VB : ViewBinding,
        D : VRODestination,
        E : VROEvent,
        > : VROFragmentInjection<VM>(), VROFragmentBasics<VM, S, D, E> {

    private var _binding: VB? = null

    val binding get() = _binding!!

    override val state: S? by lazy { restoreArguments() }

    abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    abstract fun VB.onViewStarted()

    abstract fun onViewUpdate(binding: VB, data: S)

    abstract fun VB.onError(error: Throwable)

    abstract fun VB.oneTimeHandler(id: Int, state: S)

    private lateinit var observer: LifecycleEventObserver
    private lateinit var stepperFlow: Job
    private lateinit var navigationFLow: Job
    private lateinit var oneTimeFlow: Job

    private fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): View {
        _binding = createViewBinding(inflater, container)
        return binding.root
    }

    private fun setViewBindingObservers() {
        stepperFlow = lifecycleScope.launch {
            viewModel.stepper.collectLatest { stepper ->
                when (stepper) {
                    is VROStepper.VRODialogStep -> onLoadDialog(stepper.dialogState)
                    is VROStepper.VROErrorStep -> binding.onError(stepper.error)
                    is VROStateStep -> onViewUpdate(binding, stepper.state)
                    else -> Unit
                }
            }
        }
        navigationFLow = lifecycleScope.launch {
            viewModel.getNavigationState().collectLatest {
                it?.destination?.let { destination ->
                    if (!destination.isNavigated) {
                        navigator.navigate(destination)
                        destination.setNavigated()
                    }
                } ?: navigator.navigateBack(it?.backResult)
            }
        }
        oneTimeFlow = lifecycleScope.launch {
            viewModel.getOneTimeEvents().collectLatest { oneTime ->
                if (oneTime is VROOneTimeState.Launch) {
                    binding.oneTimeHandler(oneTime.id, oneTime.state)
                    viewModel.clearOneTime()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun restoreArguments(): S? = arguments?.getSerializable(NAVIGATION_STATE) as? S

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeState(viewModel, this)
        observer = createLifecycleEventObserver(
            onCreate = {
                viewModel.onStarter(getStarterParam(findNavController().currentDestination?.id.toString()))
            },
            onStart = {
                viewModel.onStart()
            },
            onResume = {
                viewModel.onResume()
                setViewBindingObservers()
            },
            onPause = {
                cancelFlows()
            },
            onDestroy = {
                lifecycle.removeObserver(observer)
            }
        )
        lifecycle.addObserver(observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return createBinding(inflater, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getOnNavResult()
        binding.onViewStarted()
    }

    private fun initializeState(viewModel: VM, fragment: Fragment) {
        viewModel.setInitialState(state)
    }

    private fun getOnNavResult() {
        getResultParam(findNavController().currentDestination?.id.toString())?.let {
            viewModel.onNavResult(it)
        }
    }

    private fun cancelFlows() {
        stepperFlow.cancel()
        navigationFLow.cancel()
        oneTimeFlow.cancel()
    }
}