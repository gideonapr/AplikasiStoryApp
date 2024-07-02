package com.example.aplikasistoryapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.aplikasistoryapp.api.ApiService
import com.example.aplikasistoryapp.model.Story
import com.example.aplikasistoryapp.preference.LoginPreference

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val loginPreferences: LoginPreference,
    private val apiService: ApiService
) : RemoteMediator<Int, Story>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Story>
    ): MediatorResult {
        val currentPage = INITIAL_PAGE_INDEX
        val token = loginPreferences.getUser().token.orEmpty()

        return try {
            val response = apiService.getStories(
                "Bearer $token",
                currentPage,
                state.config.pageSize,
                0
            )

            if (response.isSuccessful) {
                val stories = response.body()?.listStory.orEmpty()
                val endOfPaginationReached = stories.isEmpty()
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                MediatorResult.Error(Exception("Unable to load stories"))
            }
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}
