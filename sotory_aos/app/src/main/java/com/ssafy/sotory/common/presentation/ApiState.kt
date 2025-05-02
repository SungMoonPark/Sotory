package com.ssafy.sotory.common.presentation

sealed class ApiState {
    data object Idle : ApiState()
    data object Loading : ApiState()
    data object Success : ApiState()
    data class Error(val message: String) : ApiState()
}
