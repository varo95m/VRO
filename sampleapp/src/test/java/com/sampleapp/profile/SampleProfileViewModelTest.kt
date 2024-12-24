package com.sampleapp.profile

import com.sampleapp.ui.profile.*
import com.vro.core_android.testing.VROViewModelTest
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SampleProfileViewModelTest : VROViewModelTest<SampleProfileState, SampleProfileViewModel, SampleProfileEvents>() {

    override fun onSetupInitialState() = SampleProfileState.INITIAL

    override fun onSetupViewModel() = SampleProfileViewModel()

    private val textMock = "testMock"

    @Test
    fun `onStarter should update screen state`() {
        viewModel.onStarter(SampleProfileNavStarter.Initialize(userId = textMock))
        getDataState().also {
            assertEquals(textMock, it.userId)
        }
    }
}