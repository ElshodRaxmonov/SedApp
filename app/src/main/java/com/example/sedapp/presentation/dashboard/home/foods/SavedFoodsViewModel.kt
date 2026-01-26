package com.example.sedapp.presentation.dashboard.home.foods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.usecase.bag.AddItemToBagUseCase
import com.example.sedapp.domain.usecase.bag.ObserveBagItemsUseCase
import com.example.sedapp.domain.usecase.bag.UpdateItemQuantityUseCase
import com.example.sedapp.domain.usecase.home.food.GetLikedFoodsUseCase
import com.example.sedapp.domain.usecase.home.food.ToggleLikeFoodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SavedFoodsViewModel @Inject constructor(
    private val getLikedFoodsUseCase: GetLikedFoodsUseCase,
    private val toggleLikeFoodUseCase: ToggleLikeFoodUseCase,
    private val addItemToBagUseCase: AddItemToBagUseCase,
    private val observeBagItemsUseCase: ObserveBagItemsUseCase,
    private val updateItemQuantityUseCase: UpdateItemQuantityUseCase
) : ViewModel() {

    val foodStates: StateFlow<List<FoodItemState>> = combine(
        getLikedFoodsUseCase(),
        observeBagItemsUseCase()
    ) { likedFoods, bagItems ->
        val bagMap = bagItems.associateBy({ it.food.foodId }, { it.quantity })
        likedFoods.map { food ->
            FoodItemState(
                food = food,
                quantity = bagMap[food.foodId] ?: 0,
                isLiked = true 
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleLike(food: Food) {
        viewModelScope.launch {
            // isCurrentlyLiked is true because this screen only shows liked foods
            toggleLikeFoodUseCase(food, isCurrentlyLiked = true)
        }
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
}
