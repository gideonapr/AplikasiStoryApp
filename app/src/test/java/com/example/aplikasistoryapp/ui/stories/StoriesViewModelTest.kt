package com.example.aplikasistoryapp.ui.stories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.aplikasistoryapp.adapter.ListStoryAdapter
import com.example.aplikasistoryapp.data.StoryRepository
import com.example.aplikasistoryapp.model.Story
import com.example.aplikasistoryapp.utils.DataDummy
import com.example.aplikasistoryapp.utils.MainDispatcherRule
import com.example.aplikasistoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Suppress("DEPRECATION")
@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class StoriesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private val dummyStory = DataDummy.generateDummyStory()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Story Should Not Null and Return Success`() = mainDispatcherRule.runBlockingTest {
        // Mock data from repository
        val data: PagingData<Story> = StoryPagingSource.snapshot(dummyStory.listStory)
        val expectedResponse = MutableLiveData<PagingData<Story>>()
        expectedResponse.value = data

        `when`(storyRepository.getListStories()).thenReturn(expectedResponse)

        // Initialize ViewModel
        val storiesViewModel = StoryVM(storyRepository)
        val actualStory: PagingData<Story> = storiesViewModel.getListStory.getOrAwaitValue()

        // Assert statements
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.listStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory.listStory, differ.snapshot())
        Assert.assertEquals(dummyStory.listStory[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when Get Story is Empty Should Return No Data`() = mainDispatcherRule.runBlockingTest {
        // Mock empty data from repository
        val data: PagingData<Story> = StoryPagingSource.snapshot(emptyList())
        val expectedResponse = MutableLiveData<PagingData<Story>>()
        expectedResponse.value = data

        `when`(storyRepository.getListStories()).thenReturn(expectedResponse)

        // Initialize ViewModel
        val storiesViewModel = StoryVM(storyRepository)
        val actualStory: PagingData<Story> = storiesViewModel.getListStory.getOrAwaitValue()

        // Assert statements
        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertTrue(differ.snapshot().isEmpty())
    }

    companion object {
        class StoryPagingSource(private val items: List<Story>) : PagingSource<Int, Story>() {

            companion object {
                fun snapshot(items: List<Story>): PagingData<Story> {
                    return PagingData.from(items)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
                return LoadResult.Page(
                    data = items,
                    prevKey = null,
                    nextKey = null
                )
            }
        }

        private val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {}
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }
    }
}
