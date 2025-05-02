package com.ssafy.sotory.di


import com.ssafy.sotory.data.auth.AuthDataSource
import com.ssafy.sotory.data.auth.AuthRemoteDataSource
import com.ssafy.sotory.data.creditcard.CreditCardDataSource
import com.ssafy.sotory.data.creditcard.CreditCardRemoteDataSource
import com.ssafy.sotory.data.diary.DiaryDataSource
//import com.ssafy.sotory.data.diary.DiaryRemoteDataSource
import com.ssafy.sotory.data.myroom.MyRoomDataSource
import com.ssafy.sotory.data.myroom.MyRoomRemoteDataSource
//import com.ssafy.sotory.data.payment.PaymentDataSource
//import com.ssafy.sotory.data.payment.PaymentRemoteDataSource
import com.ssafy.sotory.data.settings.SettingsDataSource
import com.ssafy.sotory.data.settings.SettingsRemoteDataSource
import com.ssafy.sotory.data.userinfo.UserInfoDataSource
import com.ssafy.sotory.data.userinfo.UserInfoRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {

//    @Binds
//    @Singleton
//    abstract fun bindPaymentDataSource(paymentRemoteDataSource: PaymentRemoteDataSource): PaymentDataSource

//    @Binds
//    @Singleton
//    abstract fun bindDiaryDataSource(diaryRemoteDataSource: DiaryRemoteDataSource): DiaryDataSource

    @Binds
    @Singleton
    abstract fun bindAuthDataSource(authRemoteDataSource: AuthRemoteDataSource): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindMyRoomDataSource(myRoomRemoteDataSource: MyRoomRemoteDataSource): MyRoomDataSource

    @Binds
    @Singleton
    abstract fun bindCreditCardDataSource(creditCardRemoteDataSource: CreditCardRemoteDataSource): CreditCardDataSource

    @Binds
    @Singleton
    abstract fun bindUserInfoDataSource(userInfoRemoteDataSource: UserInfoRemoteDataSource): UserInfoDataSource

    @Binds
    @Singleton
    abstract fun bindSettingsDataSource(settingsRemoteDataSource: SettingsRemoteDataSource): SettingsDataSource

}