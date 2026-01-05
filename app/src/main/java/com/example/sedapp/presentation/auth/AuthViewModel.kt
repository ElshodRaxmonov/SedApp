package com.example.sedapp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.User
import com.example.sedapp.domain.repository.PreferencesRepository
import com.example.sedapp.domain.usecase.auth.GoogleSignInUseCase
import com.example.sedapp.domain.usecase.auth.SignInEmailUseCase
import com.example.sedapp.domain.usecase.auth.SignUpEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User, val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInEmail: SignInEmailUseCase,
    private val signUpEmail: SignUpEmailUseCase,
    private val signInGoogle: GoogleSignInUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state = _state.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthUiState.Loading
            signInEmail(email, password)
                .onSuccess { user ->
                    val firstName = user.name
                        ?.split(" ")
                        ?.firstOrNull()
                        ?: "User"

                    _state.value = AuthUiState.Success(user, "Welcome $firstName")
                }
                .onFailure { exception ->
                    _state.value = AuthUiState.Error(
                        exception.message ?: "Authentication failed"
                    )
                }
            preferencesRepository.setFirstLaunchCompleted()
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
            preferencesRepository.setFirstLaunchCompleted()
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