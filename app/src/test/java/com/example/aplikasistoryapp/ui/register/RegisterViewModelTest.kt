package com.example.aplikasistoryapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.aplikasistoryapp.data.Result
import com.example.aplikasistoryapp.data.StoryRepository
import com.example.aplikasistoryapp.model.RegisterResponse
import com.example.aplikasistoryapp.utils.DataDummy
import com.example.aplikasistoryapp.utils.getOrAwaitValue
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var registerViewModel: RegisterVM
    private var dummyRegisterResponse = DataDummy.generateDummyRegisterSuccess()

    private val dummyName = "name"
    private val dummyEmail = "email"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        registerViewModel = RegisterVM(storyRepository)
    }

    @Test
    fun `when Post Register Should Not Null and Return Success`() {
        val expectedRegister = MutableLiveData<Result<RegisterResponse>>()
        expectedRegister.value = Result.Success(dummyRegisterResponse)
        `when`(storyRepository.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedRegister)

        val actualResponse = registerViewModel.postRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(storyRepository).register(dummyName, dummyEmail, dummyPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Post Register Should Null and Return Error`() {
        dummyRegisterResponse = DataDummy.generateDummyRegisterError()

        val expectedRegister = MutableLiveData<Result<RegisterResponse>>()
        expectedRegister.value = Result.Error("bad request")
        `when`(storyRepository.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedRegister)

        val actualResponse = registerViewModel.postRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()

        Mockito.verify(storyRepository).register(dummyName, dummyEmail, dummyPassword)
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}