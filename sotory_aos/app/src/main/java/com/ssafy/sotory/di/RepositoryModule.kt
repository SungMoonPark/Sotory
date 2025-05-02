package com.ssafy.sotory.di

import com.ssafy.sotory.data.auth.AuthRepositoryImpl
import com.ssafy.sotory.data.creditcard.CreditCardRepositoryImpl
import com.ssafy.sotory.data.diary.DiaryRepositoryImpl
import com.ssafy.sotory.data.myroom.MyRoomRepositoryImpl
import com.ssafy.sotory.data.payment.PaymentRepositoryImpl
import com.ssafy.sotory.data.settings.SettingsRepositoryImpl
import com.ssafy.sotory.data.userinfo.UserInfoRepositoryImpl
import com.ssafy.sotory.domain.auth.AuthRepository
import com.ssafy.sotory.domain.creditcard.CreditCardRepository
import com.ssafy.sotory.domain.diary.DiaryRepository
import com.ssafy.sotory.domain.myroom.MyRoomRepository
import com.ssafy.sotory.domain.payment.PaymentRepository
import com.ssafy.sotory.domain.settings.SettingsRepository
import com.ssafy.sotory.domain.userinfo.UserInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(paymentRepositoryImpl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(diaryRepositoryImpl: DiaryRepositoryImpl): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMyRoomRepository(myRoomRepositoryImpl: MyRoomRepositoryImpl): MyRoomRepository

    @Binds
    @Singleton
    abstract fun bindCreditCardRepository(creditCardRepositoryImpl: CreditCardRepositoryImpl): CreditCardRepository

    @Binds
    @Singleton
    abstract fun bindUserInfoRepository(userInfoRepositoryImpl: UserInfoRepositoryImpl): UserInfoRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(settingsRepositoryImpl: SettingsRepositoryImpl): SettingsRepository
    


}