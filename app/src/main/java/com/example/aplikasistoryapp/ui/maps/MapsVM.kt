package com.example.aplikasistoryapp.ui.maps

import androidx.lifecycle.ViewModel
import com.example.aplikasistoryapp.data.StoryRepository

class MapsVM(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories() = storyRepository.getStories()
}