package com.ssafy.sotory.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*

{
	"code": 201,
	"isSuccess": true,
	"result": {
		"menuId": number
	},
}
 */
@Serializable
data class ErrorResponse(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
)
