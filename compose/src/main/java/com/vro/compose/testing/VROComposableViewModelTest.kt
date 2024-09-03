package com.vro.compose.testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vro.compose.VROComposableViewModel
import com.vro.event.VROEvent
import com.vro.event.VROEventListener
import com.vro.navigation.VRODestination
import com.vro.state.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.mockito.MockitoAnnotations
import org.mockito.exceptions.misusing.MissingMethodInvocationException
import java.io.Serializable
import kotlin.reflect.KClass

abstract class VROComposableViewModelTest<S : VROState, VM : VROComposableViewModel<S, *, E>, E : VROEvent> {

    lateinit var viewModel: VM

    private lateinit var eventLauncher: VROEventListener<E>

    fun event(event: E) {
        eventLauncher.eventListener(event)
    }

    @Before
    open fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = onSetupViewModel()
        viewModel.concurrencyManager = VROTestDefaultConcurrencyManager()
        eventLauncher = viewModel
    }

    abstract fun onSetupViewModel(): VM

    abstract fun onSetupInitialState(): S

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    protected fun getDataState(): S {
        return viewModel.checkDataState()
    }

    fun verifyDialog(dialogType: Int) {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.stepper.firstOrNull()?.let {
                    assertEquals((it as? VROStepper.VRODialogStep<S>)?.dialogState?.type, dialogType)
                }
            } ?: throw MissingMethodInvocationException("updateDialogState not being called with this type")
        }
    }

    fun verifyError() {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.stepper.firstOrNull()?.let {
                    assertEquals(it::class.java, VROStepper.VROErrorStep::class.java)
                }
            } ?: throw MissingMethodInvocationException("updateError not being called")
        }
    }

    fun verifyError(error: Throwable) {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.stepper.firstOrNull()?.let {
                    assertEquals((it as? VROStepper.VROErrorStep<S>)?.error, error)
                }
            } ?: throw MissingMethodInvocationException("updateError not being called with this error")
        }
    }

    fun verifyUpdateScreen() {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.stepper.firstOrNull()?.let {
                    assertEquals(it::class.java, VROStepper.VROStateStep::class.java)
                }
            } ?: throw MissingMethodInvocationException("updateScreen not being called")
        }
    }

    fun verifyNavigation(destination: VRODestination) {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.navigationState.firstOrNull()?.destination?.let {
                    assertEquals(it, destination)
                }
            } ?: throw MissingMethodInvocationException("navigate not being called with this destination")
        }
    }

    fun verifyNavigation(destination: KClass<*>) {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.navigationState.firstOrNull()?.destination?.let {
                    assertEquals(it::class, destination)
                }
            } ?: throw MissingMethodInvocationException("navigate not being called with this destination")
        }
    }

    fun verifyOneTime(id: Int) {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.oneTime.firstOrNull()?.let {
                    assertEquals((it as VROSingleLaunchState.Launch).id, id)
                }
            } ?: throw MissingMethodInvocationException("updateDialogState not being called with this type")
        }
    }

    fun verifyNoNavigation(destination: KClass<*>) {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.navigationState.first()?.destination?.let {
                    assertNotEquals(it::class, destination)
                }
            } ?: throw MissingMethodInvocationException("navigate not being called with this destination")
        }
    }

    fun verifyNavigateBack(result: Serializable? = null) {
        runTest {
            withTimeoutOrNull(5000) {
                viewModel.navigationState.firstOrNull()?.let {
                    assertNull(it.destination)
                    it.backResult?.let { backResult ->
                        assertEquals(backResult, result)
                    }
                }
            } ?: throw MissingMethodInvocationException("navigateBack not being called")
        }
    }
}
