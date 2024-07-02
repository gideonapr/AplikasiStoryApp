package com.example.aplikasistoryapp.api

import com.example.aplikasistoryapp.model.CreateResponse
import com.example.aplikasistoryapp.model.LoginResponse
import com.example.aplikasistoryapp.model.RegisterResponse
import com.example.aplikasistoryapp.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @GET("stories")
    @Headers("Content-Type:application/json; charset=UTF-8")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): Response<StoryResponse>

    @GET("stories")
    @Headers("Content-Type:application/json; charset=UTF-8")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): StoryResponse

    @Multipart
    @POST("stories")
    suspend fun createStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double,
        @Part("lon") lon: Double,
    ): CreateResponse
}