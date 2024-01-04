package com.ryan.dicodingstorycompose.data.repository

import com.ryan.dicodingstorycompose.common.Resource
import com.ryan.dicodingstorycompose.data.remote.DicodingApi
import com.ryan.dicodingstorycompose.data.session.Session
import com.ryan.dicodingstorycompose.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: DicodingApi,
    private val session: Session,
) : AuthRepository {

    override fun registerUser(
        name: String,
        email: String,
        password: String,
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.registerUser(name, email, password)
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

    override fun loginUser(email: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.loginUser(email, password)
            if (response.error) {
                emit(Resource.Error(response.message))
                return@flow
            }
            response.loginResult?.let { loginDto ->
                session.saveToken(loginDto.token)
                emit(Resource.Success(response.message))
            }
        } catch (e: IOException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error occurred"))
        }
    }
}