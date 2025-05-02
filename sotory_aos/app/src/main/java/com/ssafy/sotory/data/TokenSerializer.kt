package com.ssafy.sotory.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.ssafy.sotory.TokenProto
import java.io.InputStream
import java.io.OutputStream

object TokenSerializer : Serializer<TokenProto> {
    override val defaultValue: TokenProto = TokenProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): TokenProto {
        try {
            return TokenProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: TokenProto, output: OutputStream) = t.writeTo(output)

//    val Context.settingsTokenData: DataStore<TokenProto> by dataStore(
//        fileName = "tokendata.pb", //로컬에 저장될 protobuf 파일명
//        serializer = UserDataSerializer
//    )
}