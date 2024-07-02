package com.example.aplikasistoryapp.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.aplikasistoryapp.data.StoryRepository
import com.example.aplikasistoryapp.model.Story

class StoryVM(repo: StoryRepository): ViewModel() {
    val getListStory: LiveData<PagingData<Story>> =
        repo.getListStories().cachedIn(viewModelScope)
}