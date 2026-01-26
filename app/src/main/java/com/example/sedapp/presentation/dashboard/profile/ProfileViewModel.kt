package com.example.sedapp.presentation.dashboard.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.User
import com.example.sedapp.domain.repository.AuthRepository
import com.example.sedapp.domain.usecase.auth.SignOutUseCase
import com.example.sedapp.domain.usecase.home.food.DeleteAllLikedFoodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val logoutUseCase: SignOutUseCase,
    private val deleteLikedFoodsUseCase: DeleteAllLikedFoodsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<User?>(null)
    val state = _state.asStateFlow()

    init {
        _state.value = authRepository.getCurrentUser()
    }

    fun signOut() {
        viewModelScope.launch {
            deleteLikedFoodsUseCase()
            logoutUseCase()
        }
    }
}
