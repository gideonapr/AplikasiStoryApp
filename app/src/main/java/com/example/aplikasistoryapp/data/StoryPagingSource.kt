package com.example.aplikasistoryapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.aplikasistoryapp.api.ApiService
import com.example.aplikasistoryapp.model.Story
import com.example.aplikasistoryapp.preference.LoginPreference

class StoryPagingSource(
    private val loginPreferences: LoginPreference,
    private val apiService: ApiService
) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val currentPage = params.key ?: INITIAL_PAGE_INDEX
            val token = loginPreferences.getUser().token.orEmpty()

            if (token.isNotEmpty()) {
                val response = apiService.getStories("Bearer $token", currentPage, params.loadSize, 0)
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory ?: emptyList()
                    LoadResult.Page(
                        data = stories,
                        prevKey = if (currentPage == INITIAL_PAGE_INDEX) null else currentPage - 1,
                        nextKey = if (stories.isEmpty()) null else currentPage + 1
                    )
                } else {
                    LoadResult.Error(Exception("Unable to load stories"))
                }
            } else {
                LoadResult.Error(Exception("Token is missing"))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.let { page ->
                page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}

