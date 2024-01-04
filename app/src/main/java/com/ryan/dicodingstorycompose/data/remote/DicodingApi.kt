package com.ryan.dicodingstorycompose.data.remote

import com.ryan.dicodingstorycompose.data.remote.dto.BaseResponse
import com.ryan.dicodingstorycompose.data.remote.dto.LoginResponse
import com.ryan.dicodingstorycompose.data.remote.dto.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface DicodingApi {

    @POST("register")
    @FormUrlEncoded
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): BaseResponse

    @POST("login")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    /**
     * @param location
     * - 1 = all stories with location coordinate
     * - 0 = all stories without considering location
     */
    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int = 0,
    ): StoryListResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null,
    ): BaseResponse

    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"
    }
}