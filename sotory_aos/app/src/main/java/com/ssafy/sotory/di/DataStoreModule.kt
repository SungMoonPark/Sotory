package com.ssafy.sotory.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.ssafy.sotory.data.datastore.ProfileDataStore
import com.ssafy.sotory.TokenProto
import com.ssafy.sotory.data.TokenSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providesTokenDataStore(
        @ApplicationContext context: Context,
    ): DataStore<TokenProto> = DataStoreFactory.create(
        serializer = TokenSerializer, produceFile = {
            context.dataStoreFile("token.pb")
        }, scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    )

    @Provides
    @Singleton
    fun providePaymentDataStore(
        @ApplicationContext context: Context,
    ): ProfileDataStore {
        return ProfileDataStore(context)
    }
}