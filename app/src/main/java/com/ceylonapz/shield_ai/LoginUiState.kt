package com.ceylonapz.shield_ai

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data class Success(val email: String) : LoginUiState
    data class Error(val message: String) : LoginUiState
}