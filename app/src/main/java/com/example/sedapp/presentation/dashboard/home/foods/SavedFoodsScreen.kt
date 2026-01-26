package com.example.sedapp.presentation.dashboard.home.foods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sedapp.presentation.dashboard.component.FoodItem
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedFoodsScreen(
    onBackClicked: () -> Unit,
    viewModel: SavedFoodsViewModel = hiltViewModel()
) {
    val savedFoods by viewModel.foodStates.collectAsState()

    Scaffold(
        topBar = {
            SedAppTopBar(
                title = "Saved Foods",
                onBackClicked = onBackClicked
            )
        }
    ) { paddingValues ->
        if (savedFoods.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No saved foods yet")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(savedFoods) { state ->
                    FoodItem(
                        food = state.food,
                        quantity = state.quantity,
                        isLiked = true,
                        onFoodClicked = { /* Navigate to detail if needed */ },
                        onIncrement = { viewModel.onIncrement(state.food, state.quantity) },
                        onDecrement = { viewModel.onDecrement(state.food, state.quantity) },
                        onToggleLike = {
                            viewModel.toggleLike(state.food)
                        }
                    )
                }
            }
        }
    }
}
