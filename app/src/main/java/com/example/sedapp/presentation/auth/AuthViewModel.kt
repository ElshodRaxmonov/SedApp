package com.example.sedapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.auth.usecase.GoogleSignInUseCase
import com.example.sedapp.domain.auth.usecase.SignInEmailUseCase
import com.example.sedapp.domain.auth.usecase.SignUpEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInEmail: SignInEmailUseCase,
    private val signUpEmail: SignUpEmailUseCase,
    private val signInGoogle: GoogleSignInUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state = _state.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            signInEmail(email, password)
                .onSuccess { user ->
                    val firstName = user.name!!.split(" ").firstOrNull()
                    _state.value = AuthUiState.Success(user, "Welcome $firstName")
                }
                .onFailure { exception ->
                    _state.value = AuthUiState.Error(
                        "This account is not available. Please sign up first"
                    )
                }
        }
    }

    fun signUpWithEmail(name: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            signUpEmail(name, email, password)
                .onSuccess { user ->
                    _state.value =
                        AuthUiState.Success(user, "Welcome ${user.name!!.split(" ").firstOrNull()}")
                }
                .onFailure { exception ->
                    _state.value = AuthUiState.Error(
                        exception.message ?: "Sign up failed"
                    )
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            signInGoogle(idToken)
                .onSuccess { user ->
                    _state.value = AuthUiState.Success(user, "")
                }
                .onFailure { exception ->
                    _state.value = AuthUiState.Error(
                        exception.message ?: "Google sign in failed"
                    )
                }
        }
    }

    fun resetState() {
        _state.value = AuthUiState.Idle
    }
}