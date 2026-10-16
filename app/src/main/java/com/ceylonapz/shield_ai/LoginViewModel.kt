package com.ceylonapz.shield_ai

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface LoginRepo {
    suspend fun loginNow(email: String): String
}

class LoginViewModel(private val repo: LoginRepo) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState = _uiState.asStateFlow()

    suspend fun login(email: String) {
        _uiState.value = LoginUiState.Loading

        try {
            val result = repo.loginNow(email)
            _uiState.value = LoginUiState.Success(result)
        }
        catch (ex : Exception){
            _uiState.value = LoginUiState.Error(ex.message ?: "Unknown Error")
        }
    }
}