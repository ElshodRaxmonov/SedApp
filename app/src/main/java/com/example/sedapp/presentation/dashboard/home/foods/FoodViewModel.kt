package com.example.sedapp.presentation.dashboard.home.foods

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Category
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.usecase.GetCategoriesUseCase
import com.example.sedapp.domain.usecase.bag.AddItemToBagUseCase
import com.example.sedapp.domain.usecase.bag.ObserveBagItemsUseCase
import com.example.sedapp.domain.usecase.bag.UpdateItemQuantityUseCase
import com.example.sedapp.domain.usecase.home.food.GetFoodsUseCase
import com.example.sedapp.domain.usecase.home.food.GetLikedFoodsUseCase
import com.example.sedapp.domain.usecase.home.food.ToggleLikeFoodUseCase
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
    val quantity: Int = 0,
    val isLiked: Boolean = false
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
    private val updateItemQuantityUseCase: UpdateItemQuantityUseCase,
    private val getLikedFoodsUseCase: GetLikedFoodsUseCase,
    private val toggleLikeFoodUseCase: ToggleLikeFoodUseCase,
    private val savedStateHandle: SavedStateHandle
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
        observeBagItemsUseCase(),
        getLikedFoodsUseCase()
    ) { foods, categories, filters, bagItems, likedFoods ->
        val bagMap = bagItems.associateBy({ it.food.foodId }, { it.quantity })
        val likedSet = likedFoods.map { it.foodId }.toSet()

        val filteredFoods = foods.filter { food ->
            val matchesHalal = !filters.isHalalOnly || food.isHalal
            val matchesCategory =
                filters.selectedCategories.isEmpty() || food.category in filters.selectedCategories
            matchesHalal && matchesCategory
        }.map { FoodItemState(it, bagMap[it.foodId] ?: 0, it.foodId in likedSet) }

        // Map categories to show their selection state in the UI
        val categoriesWithSelection = categories.map { category ->
            category.copy(isCategorySelected = category.name in filters.selectedCategories)
        }

        FoodUiState(
            isLoading = false,
            availableCategories = categoriesWithSelection,
            displayedFoods = filteredFoods,
            selectedCategoryNames = filters.selectedCategories,
            isHalalOnly = filters.isHalalOnly
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FoodUiState(isLoading = true))

    init {
        loadInitialData()
        // Check for category passed from navigation
        savedStateHandle.get<String>("categoryName")?.let { name ->
            if (name.isNotEmpty()) {
                onCategorySelected(name)
            }
        }
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

    fun onIncrement(food: Food, currentQty: Int) {
        viewModelScope.launch {
            if (currentQty == 0) {
                addItemToBagUseCase(food, 1)
            } else {
                updateItemQuantityUseCase(food.foodId, 1)
            }
        }
    }

    fun onDecrement(food: Food, currentQty: Int) {
        viewModelScope.launch {
            if (currentQty > 0) {
                updateItemQuantityUseCase(food.foodId, -1)
            }
        }
    }

    fun addToBag(food: Food, quantity: Int) {
        viewModelScope.launch {
            addItemToBagUseCase(food, quantity)
        }
    }

    fun toggleLike(food: Food, isLiked: Boolean) {
        viewModelScope.launch {
            toggleLikeFoodUseCase(food, isLiked)
        }
    }
}
