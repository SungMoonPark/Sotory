package com.ssafy.sotory.data

import androidx.datastore.core.DataStore
import com.ssafy.sotory.TokenProto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<TokenProto>,
) {
    private var cachedTempToken: String? = null
    private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null
    private var storeId: String? = null
    var cachedStoreId: Long? = null

    private var _SotoryToken: String? = null

    // 이벤트를 알리기 위한 SharedFlow
    private val _tokenClearedEvent = MutableSharedFlow<Unit>()
    val tokenClearedEvent: SharedFlow<Unit> = _tokenClearedEvent

    suspend fun getTempToken(): String? {
        return if (cachedTempToken == null) {
            dataStore.data.map { token ->
                token.tempToken.takeIf { it.isNotEmpty() }
            }.first()
        } else {
            cachedTempToken
        }
    }

    suspend fun getAccessToken(): String? {
        return if (cachedAccessToken == null) {
            dataStore.data.map { token ->
                token.accessToken.takeIf { it.isNotEmpty() }
            }.first()
        } else {
            cachedAccessToken
        }
    }

    suspend fun getToken(): String? {
        return if (cachedAccessToken != null) {
            cachedAccessToken
        } else if (cachedTempToken != null) {
            cachedTempToken
        } else if (getAccessToken() != null) {
            getAccessToken()
        } else {
            getTempToken()
        }
    }

    suspend fun getRefreshToken(): String? {
        return if (cachedRefreshToken == null) {
            dataStore.data.map { token ->
                token.refreshToken.takeIf { it.isNotEmpty() }
            }.first()
        } else {
            cachedRefreshToken
        }
    }

    suspend fun getStoreId(): String? {
        return if (storeId == null) {
            val sId = dataStore.data.map { token ->
                token.storeId.takeIf { it.isNotEmpty() }
            }.first()
            if (sId != null) {
                cachedStoreId = sId.toLong()
            }
            sId
        } else {
            cachedStoreId = storeId!!.toLong()
            storeId
        }
    }

    fun getSotoryToken(): String? {
        return _SotoryToken
    }

    suspend fun saveTempToken(token: String) {
        cachedTempToken = token
        dataStore.updateData { currentToken ->
            currentToken.toBuilder().setTempToken(token).build()
        }
    }

    suspend fun saveAccessToken(token: String) {
        cachedAccessToken = token
        dataStore.updateData { currentToken ->
            currentToken.toBuilder().setAccessToken(token).build()
        }
    }

    suspend fun saveRefreshToken(token: String) {
        cachedRefreshToken = token
        dataStore.updateData { currentToken ->
            currentToken.toBuilder().setRefreshToken(token).build()
        }
    }

    suspend fun saveStoreId(storeId: String) {
        this.storeId = storeId
        cachedStoreId = storeId.toLong()
        dataStore.updateData { currentToken ->
            currentToken.toBuilder().setStoreId(storeId).build()
        }
    }

    suspend fun clearStoreId() {
        dataStore.updateData {
            it.toBuilder().clearStoreId().build()
        }
        cachedStoreId = null
    }


    suspend fun clearTempToken() {
        dataStore.updateData {
            it.toBuilder().clearTempToken().build()
        }
    }

    suspend fun clearTokens() {
        dataStore.updateData {
            it.toBuilder().clear().build()
        }
        cachedAccessToken = null
        cachedRefreshToken = null
        cachedTempToken = null
        // 이벤트 발생
        _tokenClearedEvent.emit(Unit)
    }

    fun clearSotoryToken() {
        _SotoryToken = null
    }

    suspend fun hasValidToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    fun saveCacheAccessToken(token: String) {
        cachedAccessToken = token
    }

    fun saveSotoryToken(token: String) {
        _SotoryToken = token
    }

}