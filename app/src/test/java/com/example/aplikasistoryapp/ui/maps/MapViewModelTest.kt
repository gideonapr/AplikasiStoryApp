package com.example.aplikasistoryapp.ui.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.aplikasistoryapp.data.Result
import com.example.aplikasistoryapp.data.StoryRepository
import com.example.aplikasistoryapp.model.StoryResponse
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
class MapViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapViewModel: MapsVM
    private var dummyStory = DataDummy.generateDummyStory()

    @Before
    fun setUp() {
        mapViewModel = MapsVM(storyRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<Result<StoryResponse>>()
        expectedResponse.value = Result.Success(dummyStory)
        `when`(storyRepository.getStories()).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getStories().getOrAwaitValue()

        Mockito.verify(storyRepository).getStories()
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Success)
    }

    @Test
    fun `when Get Story Should Null and Return Error`() {
        dummyStory = DataDummy.generateErrorDummyStory()

        val expectedResponse = MutableLiveData<Result<StoryResponse>>()
        expectedResponse.value = Result.Error("error")
        `when`(storyRepository.getStories()).thenReturn(expectedResponse)

        val actualResponse = mapViewModel.getStories().getOrAwaitValue()

        Mockito.verify(storyRepository).getStories()
        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is Result.Error)
    }
}