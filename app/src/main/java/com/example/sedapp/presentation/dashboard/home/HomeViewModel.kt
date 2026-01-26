package com.example.sedapp.presentation.dashboard.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Category
import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.domain.usecase.GetCategoriesUseCase
import com.example.sedapp.domain.usecase.GetCurrentUserUseCase
import com.example.sedapp.domain.usecase.GetGreetingUseCase
import com.example.sedapp.domain.usecase.GetTopRestaurantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "Home Screen"

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val restaurants: List<Restaurant> = emptyList(),
    val greeting: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getTopRestaurantsUseCase: GetTopRestaurantsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getGreetingUseCase: GetGreetingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
        Log.d(TAG, "init: worked ${_uiState.value.restaurants}")
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {

                val user = getCurrentUserUseCase()
                val greeting = getGreetingUseCase(username = user?.name)
                val categories = async { getCategoriesUseCase() }.await()
                val restaurants = async { getTopRestaurantsUseCase() }.await()
                _uiState.value = HomeUiState(
                    isLoading = false,
                    categories = categories,
                    restaurants = restaurants,
                    greeting = greeting
                )


            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading home data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load home data"
                )
            }
        }
    }
}
