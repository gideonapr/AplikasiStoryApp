package com.example.aplikasistoryapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplikasistoryapp.data.StoryRepository
import com.example.aplikasistoryapp.di.Injection
import com.example.aplikasistoryapp.ui.create.CreateVM
import com.example.aplikasistoryapp.ui.login.LoginVM
import com.example.aplikasistoryapp.ui.maps.MapsVM
import com.example.aplikasistoryapp.ui.register.RegisterVM
import com.example.aplikasistoryapp.ui.stories.StoryVM

@Suppress("UNCHECKED_CAST")
class VMFactory private constructor(private val storyRepository: StoryRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoryVM::class.java) -> {
                StoryVM(storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginVM::class.java) -> {
                LoginVM(storyRepository) as T
            }
            modelClass.isAssignableFrom(RegisterVM::class.java) -> {
                RegisterVM(storyRepository) as T
            }
            modelClass.isAssignableFrom(CreateVM::class.java) -> {
                CreateVM(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapsVM::class.java) -> {
                MapsVM(storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var factoryInstance: VMFactory? = null
        fun getInstance(context: Context): VMFactory {
            return factoryInstance ?: synchronized(this) {
                factoryInstance ?: VMFactory(Injection.provideRepository(context)).also {
                    factoryInstance = it
                }
            }
        }
    }
}
