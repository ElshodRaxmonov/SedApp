package com.example.sedapp.presentation.dashboard.home.foods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Category
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.usecase.GetCategoriesUseCase
import com.example.sedapp.domain.usecase.bag.AddItemToBagUseCase
import com.example.sedapp.domain.usecase.bag.ObserveBagItemsUseCase
import com.example.sedapp.domain.usecase.bag.UpdateItemQuantityUseCase
import com.example.sedapp.domain.usecase.home.food.GetFoodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodItemState(
    val food: Food,
    val quantity: Int = 0
)

data class FoodUiState(
    val isLoading: Boolean = false,
    val availableCategories: List<Category> = emptyList(),
    val displayedFoods: List<FoodItemState> = emptyList(),
    val selectedCategoryNames: Set<String> = emptySet(),
    val isHalalOnly: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addItemToBagUseCase: AddItemToBagUseCase,
    private val observeBagItemsUseCase: ObserveBagItemsUseCase,
    private val updateItemQuantityUseCase: UpdateItemQuantityUseCase
) : ViewModel() {

    private val _allFoods = MutableStateFlow<List<Food>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    private val _filterState = MutableStateFlow(FilterState())

    data class FilterState(
        val selectedCategories: Set<String> = emptySet(),
        val isHalalOnly: Boolean = false
    )

    val uiState: StateFlow<FoodUiState> = combine(
        _allFoods,
        _categories,
        _filterState,
        observeBagItemsUseCase()
    ) { foods, categories, filters, bagItems ->
        val bagMap = bagItems.associateBy({ it.food.foodId }, { it.quantity })
        
        val filteredFoods = foods.filter { food ->
            val matchesHalal = !filters.isHalalOnly || food.isHalal
            val matchesCategory = filters.selectedCategories.isEmpty() || food.category in filters.selectedCategories
            matchesHalal && matchesCategory
        }.map { FoodItemState(it, bagMap[it.foodId] ?: 0) }

        FoodUiState(
            isLoading = false,
            availableCategories = categories,
            displayedFoods = filteredFoods,
            selectedCategoryNames = filters.selectedCategories,
            isHalalOnly = filters.isHalalOnly
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FoodUiState(isLoading = true))

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                _allFoods.value = getFoodsUseCase()
                _categories.value = getCategoriesUseCase()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun onCategorySelected(categoryName: String) {
        _filterState.update { state ->
            val newSelected = if (categoryName in state.selectedCategories) {
                state.selectedCategories - categoryName
            } else {
                state.selectedCategories + categoryName
            }
            state.copy(selectedCategories = newSelected)
        }
    }

    fun onHalalFilterChanged(isHalal: Boolean) {
        _filterState.update { it.copy(isHalalOnly = isHalal) }
    }

    fun addToBag(food: Food, quantity: Int) {
        viewModelScope.launch {
            addItemToBagUseCase(food, quantity)
        }
    }

    fun updateQuantity(foodId: String, delta: Int, currentQty: Int) {
        viewModelScope.launch {
            updateItemQuantityUseCase(foodId, currentQty + delta)
        }
    }
}
