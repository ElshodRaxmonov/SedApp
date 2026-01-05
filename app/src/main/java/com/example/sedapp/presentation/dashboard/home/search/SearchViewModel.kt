package com.example.sedapp.presentation.dashboard.home.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// File: ui/search/SearchViewModel.kt
sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<Restaurant>, val foods:List<Food>) : SearchUiState()
    object NotFound : SearchUiState() // Explicit state for the image provided
}

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {
    var searchQuery by mutableStateOf("")
        private set

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onQueryChange(newQuery: String) {
        searchQuery = newQuery
        // If query is cleared, go back to Idle
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
        }
    }

    fun performSearch() {
        if (searchQuery.isBlank()) return

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            // Simulate API/Firestore call
            delay(1000)

            // Logic: if search doesn't match anything, set to NotFound
            _uiState.value = SearchUiState.NotFound
        }
    }
}