package com.example.sedapp.presentation.dashboard.home.restaurants

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.domain.usecase.GetTopRestaurantsUseCase
import com.example.sedapp.domain.usecase.home.restaurant.GetCuisinesUseCase
import com.example.sedapp.domain.usecase.home.restaurant.GetRestaurantDetailsUseCase
import com.example.sedapp.domain.usecase.home.restaurant.GetRestaurantFoodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestaurantUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val availableCuisines: List<String> = emptyList(),
    val restaurants: List<Restaurant> = emptyList(),
    val selectedCuisines: Set<String> = emptySet(),
    val selectedRestaurantName: String = ""
)

data class RestaurantDetailsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val restaurant: Restaurant? = null,
    val foods: List<Food> = emptyList()
)

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    val getRestaurantsUseCase: GetTopRestaurantsUseCase,
    val getCuisinesUseCase: GetCuisinesUseCase,
    val getRestaurantDetailsUseCase: GetRestaurantDetailsUseCase,
    val getRestaurantFoodsUseCase: GetRestaurantFoodsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _detailsState = MutableStateFlow(RestaurantDetailsUiState(isLoading = true))
    val detailsState = _detailsState.asStateFlow()

    private var allRestaurants: List<Restaurant> = emptyList()

    init {
        loadRestaurantList()
        // Automatically check if we navigated here with a specific restaurant
        savedStateHandle.get<String>("restaurantName")?.let { name ->
            loadRestaurantDetails(name)
        }
    }

    private fun loadRestaurantList() {
        viewModelScope.launch {
            try {
                val restaurants = getRestaurantsUseCase()
                val cuisines = getCuisinesUseCase()
                allRestaurants = restaurants
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        restaurants = restaurants,
                        availableCuisines = cuisines.toList()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun loadRestaurantDetails(name: String) {
        viewModelScope.launch {
            _detailsState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val restaurant = getRestaurantDetailsUseCase(name)
                val foods = getRestaurantFoodsUseCase(name)
                _detailsState.update {
                    it.copy(
                        isLoading = false,
                        restaurant = restaurant,
                        foods = foods
                    )
                }
            } catch (e: Exception) {
                _detailsState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = e.message ?: "Failed to load restaurant details"
                    ) 
                }
            }
        }
    }

    fun onCuisineSelected(selectedCuisine: String) {
        val currentSelected = _uiState.value.selectedCuisines
        val newSelected = if (selectedCuisine in currentSelected) {
            currentSelected - selectedCuisine
        } else {
            currentSelected + selectedCuisine
        }
        _uiState.update { it.copy(selectedCuisines = newSelected) }
        applyFilters()
    }

    fun onRestaurantClick(restaurantName: String) {
        // This is used when clicking from the Restaurant list inside the same VM scope
        loadRestaurantDetails(restaurantName)
    }


    private fun applyFilters() {
        val currentState = _uiState.value
        var filteredList = allRestaurants

        if (currentState.selectedCuisines.isNotEmpty()) {
            filteredList = filteredList.filter { it.cuisine in currentState.selectedCuisines }
        }

        _uiState.update { it.copy(restaurants = filteredList) }
    }
}
