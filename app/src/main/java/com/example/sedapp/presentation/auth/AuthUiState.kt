package com.example.sedapp.presentation.auth

import com.example.sedapp.domain.auth.model.User

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User, val message: String ) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}