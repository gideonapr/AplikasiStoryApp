package com.example.aplikasistoryapp.ui.login

import androidx.lifecycle.ViewModel
import com.example.aplikasistoryapp.data.StoryRepository

class LoginVM(private val storyRepository: StoryRepository): ViewModel() {
    fun postLogin(email: String, password: String) = storyRepository.login(email, password)
}