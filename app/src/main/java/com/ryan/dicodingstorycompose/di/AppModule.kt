package com.ryan.dicodingstorycompose.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.ryan.dicodingstorycompose.data.remote.DicodingApi
import com.ryan.dicodingstorycompose.data.repository.AuthRepositoryImpl
import com.ryan.dicodingstorycompose.data.repository.StoryRepositoryImpl
import com.ryan.dicodingstorycompose.data.session.Session
import com.ryan.dicodingstorycompose.domain.repository.AuthRepository
import com.ryan.dicodingstorycompose.domain.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(session: Session): OkHttpClient {
        val loggingInterceptor = if (com.ryan.dicodingstorycompose.BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val token = runBlocking { session.getTokenFlow().first() }

                val requestBuilder = original.newBuilder()
                if (token.isNotBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideDicodingApi(okHttpClient: OkHttpClient): DicodingApi {
        return Retrofit.Builder()
            .baseUrl(DicodingApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(DicodingApi::class.java)
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        api: DicodingApi,
        session: Session
    ): AuthRepository {
        return AuthRepositoryImpl(api, session)
    }

    @Singleton
    @Provides
    fun provideStoryRepository(
        api: DicodingApi
    ): StoryRepository {
        return StoryRepositoryImpl(api)
    }
}