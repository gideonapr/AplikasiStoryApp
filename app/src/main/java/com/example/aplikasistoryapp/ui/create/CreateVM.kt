package com.example.aplikasistoryapp.ui.create

import androidx.lifecycle.ViewModel
import com.example.aplikasistoryapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateVM(private val storyRepository: StoryRepository): ViewModel() {
    fun postCreateStory(imageFile: MultipartBody.Part, desc: RequestBody, lat: Double, lon: Double) = storyRepository.createStory(imageFile, desc, lat, lon)
}