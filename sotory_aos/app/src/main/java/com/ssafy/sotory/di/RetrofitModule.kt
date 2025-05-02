package com.ssafy.sotory.di
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ssafy.sotory.data.auth.AuthService
import com.ssafy.sotory.data.creditcard.CreditCardService
import com.ssafy.sotory.data.diary.DiaryService
import com.ssafy.sotory.data.myroom.MyRoomService
import com.ssafy.sotory.data.payment.PaymentService
import com.ssafy.sotory.data.settings.SettingsService
import com.ssafy.sotory.data.userinfo.UserInfoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {
//    @Singleton
//    @Provides
//    fun provideAuthService(retrofit: Retrofit): AuthService =
//        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun providePaymentService(retrofit: Retrofit): PaymentService =
        retrofit.create(PaymentService::class.java)

    @Singleton
    @Provides
    fun provideDiaryService(retrofit: Retrofit): DiaryService =
        retrofit.create(DiaryService::class.java)

    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideMyRoomService(retrofit: Retrofit): MyRoomService =
        retrofit.create(MyRoomService::class.java)

    @Singleton
    @Provides
    fun provideCreditCardService(retrofit: Retrofit): CreditCardService =
        retrofit.create(CreditCardService::class.java)

    @Singleton
    @Provides
    fun provideUserInfoService(retrofit: Retrofit): UserInfoService =
        retrofit.create(UserInfoService::class.java)

    @Singleton
    @Provides
    fun provideSettingsService(retrofit: Retrofit): SettingsService =
        retrofit.create(SettingsService::class.java)
}