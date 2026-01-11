package com.example.sedapp.presentation.dashboard.home.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.domain.usecase.home.FoodSearchUseCase
import com.example.sedapp.domain.usecase.home.RestaurantSearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<Restaurant>, val foods: List<Food>) : SearchUiState()
    object NotFound : SearchUiState()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val foodSearchUseCase: FoodSearchUseCase,
    private val restaurantSearchUseCase: RestaurantSearchUseCase
) : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        searchQuery = newQuery
        searchJob?.cancel()
        
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
        } else {
            // Automatic search with debounce for better UX
            searchJob = viewModelScope.launch {
                delay(500)
                performSearch()
            }
        }
    }

    fun performSearch() {
        val query = searchQuery.trim()
        if (query.isBlank()) return

        searchJob?.cancel()
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                // Try searching with the exact query and also with first letter capitalized 
                // to mitigate Firestore's case-sensitivity issues
                val normalizedQuery = query.replaceFirstChar { it.uppercase() }
                
                val results = restaurantSearchUseCase(normalizedQuery)
                val foods = foodSearchUseCase(normalizedQuery)
                
                _uiState.value = if (results.isNotEmpty() || foods.isNotEmpty()) {
                    SearchUiState.Success(results, foods)
                } else {
                    // Fallback to exact query if normalization didn't work
                    val exactResults = restaurantSearchUseCase(query)
                    val exactFoods = foodSearchUseCase(query)
                    
                    if (exactResults.isNotEmpty() || exactFoods.isNotEmpty()) {
                        SearchUiState.Success(exactResults, exactFoods)
                    } else {
                        SearchUiState.NotFound
                    }
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.NotFound
            }
        }
    }
}