package com.example.sedapp.presentation.dashboard.home.foods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Category
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.usecase.GetCategoriesUseCase
import com.example.sedapp.domain.usecase.GetFoodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class FoodUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val foods: List<Food> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadFoodData()
    }

    private fun loadFoodData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                coroutineScope {
                    val foodsDeferred = async { getFoodsUseCase() }
                    val categoriesDeferred = async { getCategoriesUseCase() }

                    _uiState.value = FoodUiState(
                        isLoading = false,
                        foods = foodsDeferred.await(),
                        categories = categoriesDeferred.await()
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load data"
                )
            }
        }
    }
}
