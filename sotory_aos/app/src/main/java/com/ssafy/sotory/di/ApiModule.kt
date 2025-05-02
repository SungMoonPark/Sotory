package com.ssafy.sotory.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ssafy.sotory.util.NetworkUtil
import com.ssafy.sotory.util.NetworkUtil.jsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideRetrofit(
        @RestApiClientQualifier okHttpClient: OkHttpClient,
    ): Retrofit {


        return Retrofit.Builder().baseUrl(NetworkUtil.BASE_URL).client(okHttpClient)
            .addConverterFactory(jsonBuilder.asConverterFactory("application/json".toMediaType()))
//            .addConverterFactory(
//                GsonConverterFactory.create()
//            )
            .build()
    }
}
