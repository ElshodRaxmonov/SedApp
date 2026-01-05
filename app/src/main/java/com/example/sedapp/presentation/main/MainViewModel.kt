package com.example.sedapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.AppStartDestination
import com.example.sedapp.domain.repository.AuthRepository
import com.example.sedapp.domain.usecase.DetermineStartDestinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class MainUiState {
    object Loading : MainUiState()
    data class Ready(val destination: AppStartDestination) : MainUiState()
}


@HiltViewModel
class MainViewModel @Inject constructor(
    private val determineStartDestinationUseCase: DetermineStartDestinationUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        observeStartDestination()
    }

    private fun observeStartDestination() {
        viewModelScope.launch {
            authRepository.observeAuthState().collect {
                val destination = determineStartDestinationUseCase()
                _uiState.value = MainUiState.Ready(destination)
            }
        }
    }
}
