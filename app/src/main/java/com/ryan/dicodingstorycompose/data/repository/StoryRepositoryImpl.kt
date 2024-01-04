package com.ryan.dicodingstorycompose.data.repository

import com.ryan.dicodingstorycompose.common.Resource
import com.ryan.dicodingstorycompose.data.remote.DicodingApi
import com.ryan.dicodingstorycompose.data.toStory
import com.ryan.dicodingstorycompose.domain.model.Story
import com.ryan.dicodingstorycompose.domain.repository.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val api: DicodingApi,
) : StoryRepository {

    override fun getStories(hasLocation: Int): Flow<Resource<List<Story>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getStories(location = hasLocation)
            if (response.error) {
                emit(Resource.Error(response.message))
                return@flow
            }
            val stories = response.listStory.map { it.toStory() }
            emit(Resource.Success(stories))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

    override fun addStory(
        photoFile: File,
        description: String,
        lat: Double?,
        lon: Double?
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.addStory(
                photo = MultipartBody.Part.createFormData("photo", photoFile.name, photoFile.asRequestBody()),
                description = description.toRequestBody("application/json".toMediaTypeOrNull()),
                lat = lat,
                lon = lon,
            )
            if (response.error) {
                emit(Resource.Error(response.message))
                return@flow
            }
            emit(Resource.Success(response.message))
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }

}