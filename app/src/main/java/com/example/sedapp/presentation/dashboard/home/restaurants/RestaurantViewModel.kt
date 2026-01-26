package com.example.sedapp.presentation.dashboard.home.restaurants

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.domain.usecase.GetTopRestaurantsUseCase
import com.example.sedapp.domain.usecase.bag.ObserveBagItemsUseCase
import com.example.sedapp.domain.usecase.home.restaurant.GetCuisinesUseCase
import com.example.sedapp.domain.usecase.home.restaurant.GetRestaurantDetailsUseCase
import com.example.sedapp.domain.usecase.home.restaurant.GetRestaurantFoodsUseCase
import com.example.sedapp.presentation.dashboard.home.foods.FoodItemState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    val foods: List<FoodItemState> = emptyList()
)

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRestaurantsUseCase: GetTopRestaurantsUseCase,
    private val getCuisinesUseCase: GetCuisinesUseCase,
    private val getRestaurantDetailsUseCase: GetRestaurantDetailsUseCase,
    private val getRestaurantFoodsUseCase: GetRestaurantFoodsUseCase,
    private val observeBagItemsUseCase: ObserveBagItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _detailsLoading = MutableStateFlow(true)
    private val _detailsError = MutableStateFlow<String?>(null)
    private val _currentRestaurant = MutableStateFlow<Restaurant?>(null)
    private val _currentFoods = MutableStateFlow<List<Food>>(emptyList())

    val detailsState: StateFlow<RestaurantDetailsUiState> = combine(
        _detailsLoading,
        _detailsError,
        _currentRestaurant,
        _currentFoods,
        observeBagItemsUseCase()
    ) { loading, error, restaurant, foods, bagItems ->
        val bagMap = bagItems.associateBy({ it.food.foodId }, { it.quantity })
        val foodStates = foods.map { FoodItemState(it, bagMap[it.foodId] ?: 0) }
        
        RestaurantDetailsUiState(
            isLoading = loading,
            errorMessage = error,
            restaurant = restaurant,
            foods = foodStates
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RestaurantDetailsUiState(isLoading = true))

    private var allRestaurants: List<Restaurant> = emptyList()

    init {
        loadRestaurantList()
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
            _detailsLoading.value = true
            _detailsError.value = null
            try {
                val restaurant = getRestaurantDetailsUseCase(name)
                val foods = getRestaurantFoodsUseCase(name)
                _currentRestaurant.value = restaurant
                _currentFoods.value = foods
                _detailsLoading.value = false
            } catch (e: Exception) {
                _detailsError.value = e.message ?: "Failed to load restaurant details"
                _detailsLoading.value = false
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
