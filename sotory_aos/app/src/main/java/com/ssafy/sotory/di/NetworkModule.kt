package com.ssafy.sotory.di

import com.ssafy.sotory.data.AuthAuthenticator
import com.ssafy.sotory.data.AuthInterceptor
import com.ssafy.sotory.data.TokenManager
import com.ssafy.sotory.domain.auth.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RestApiClientQualifier


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideAuthAuthenticator(
        tokenManager: TokenManager,
    ): AuthAuthenticator {
        return AuthAuthenticator(
            tokenManager
        )
    }

    @Provides
    @Singleton
    @RestApiClientQualifier
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        authAuthenticator: AuthAuthenticator,
    ): OkHttpClient {
        val provideLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder().addInterceptor(authInterceptor) // Add AuthInterceptor
            .authenticator(authAuthenticator) // Add AuthAuthenticator
            .addInterceptor(provideLoggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 설정
            .readTimeout(100, TimeUnit.SECONDS) // 읽기 타임아웃 설정
            .writeTimeout(30, TimeUnit.SECONDS).build()
    }
}
