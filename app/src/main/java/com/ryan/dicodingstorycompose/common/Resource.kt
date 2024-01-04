package com.ryan.dicodingstorycompose.common

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
//    class Loading<T>(val isLoading: Boolean = true) : Resource<T>(null)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}