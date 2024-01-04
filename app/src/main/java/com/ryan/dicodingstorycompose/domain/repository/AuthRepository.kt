package com.ryan.dicodingstorycompose.domain.repository

import com.ryan.dicodingstorycompose.common.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun registerUser(
        name: String,
        email: String,
        password: String,
    ): Flow<Resource<String>>

    fun loginUser(
        email: String,
        password: String,
    ): Flow<Resource<String>>
}