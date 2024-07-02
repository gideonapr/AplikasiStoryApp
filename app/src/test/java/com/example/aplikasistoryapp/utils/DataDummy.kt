package com.example.aplikasistoryapp.utils


import com.example.aplikasistoryapp.model.CreateResponse
import com.example.aplikasistoryapp.model.LoginResponse
import com.example.aplikasistoryapp.model.LoginResultResponse
import com.example.aplikasistoryapp.model.RegisterResponse
import com.example.aplikasistoryapp.model.Story
import com.example.aplikasistoryapp.model.StoryResponse

object DataDummy {
    fun generateDummyLoginSuccess(): LoginResponse {
        return LoginResponse(
            error = false,
            message = "success",
            loginResult = LoginResultResponse(
                userId = "userId",
                name = "name",
                token = "token"
            )
        )
    }

    fun generateDummyLoginError(): LoginResponse {
        return LoginResponse(
            error = true,
            message = "invalid password"
        )
    }

    fun generateDummyRegisterSuccess(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyRegisterError(): RegisterResponse {
        return RegisterResponse(
            error = true,
            message = "bad request"
        )
    }

    fun generateDummyCreateStorySuccess(): CreateResponse {
        return CreateResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyCreateStoryError(): CreateResponse {
        return CreateResponse(
            error = true,
            message = "error"
        )
    }

    fun generateDummyStory(): StoryResponse {
        return StoryResponse(
            error = false,
            message = "success",
            listStory = arrayListOf(
                Story(
                    id = "id",
                    name = "name",
                    description = "description",
                    photoUrl = "photoUrl",
                    createdAt = "createdAt",
                    lat = 0.01,
                    lon = 0.01
                )
            )
        )
    }

    fun generateErrorDummyStory(): StoryResponse {
        return StoryResponse(
            error = true,
            message = "error",
            listStory = arrayListOf(
                Story(
                    id = "id",
                    name = "name",
                    description = "description",
                    photoUrl = "photoUrl",
                    createdAt = "createdAt",
                    lat = 0.01,
                    lon = 0.01
                )
            )
        )
    }
}