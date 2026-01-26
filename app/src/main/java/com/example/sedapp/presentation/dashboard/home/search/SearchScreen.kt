package com.example.sedapp.presentation.dashboard.home.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.domain.model.Food
import com.example.sedapp.presentation.dashboard.component.FoodItem
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar
import com.example.sedapp.presentation.dashboard.home.RestaurantCard
import com.example.sedapp.presentation.dashboard.home.foods.FoodViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    foodViewModel: FoodViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onRestaurantClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val foodUiState by foodViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var selectedFood by remember { mutableStateOf<Food?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Map food IDs to their current bag quantities and like status for reactive updates
    val quantities = remember(foodUiState.displayedFoods) {
        foodUiState.displayedFoods.associate { it.food.foodId to it.quantity }
    }
    val likedFoods = remember(foodUiState.displayedFoods) {
        foodUiState.displayedFoods.associate { it.food.foodId to it.isLiked }
    }

    Scaffold(
        topBar = {
            SedAppTopBar(
                title = "Search",
                onBackClicked = onBackClicked
            )
        },
        containerColor = Charcoal
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            SearchTextField(
                value = viewModel.searchQuery,
                onValueChange = viewModel::onQueryChange,
                onSearchAction = {
                    viewModel.performSearch()
                    focusManager.clearFocus()
                }

            )

            Spacer(Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is SearchUiState.Idle -> {
                        SearchAnimationContent(
                            animationRes = "raw/onboarding_animation.json",
                            title = "Search for food or restaurants",
                            subtitle = "Explore the best meals around you"
                        )
                    }

                    is SearchUiState.Loading -> {
                        CircularProgressIndicator(
                            color = SedAppOrange,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    is SearchUiState.NotFound -> {
                        SearchAnimationContent(
                            animationRes = "raw/not_found_animation.json",
                            title = "No results found",
                            subtitle = "Try searching for something else"
                        )
                    }

                    is SearchUiState.Success -> {
                        SearchSuccessContent(
                            uiState = state,
                            quantities = quantities,
                            likedFoods = likedFoods,
                            onRestaurantClick = onRestaurantClick,
                            onFoodClick = { food ->
                                selectedFood = food
                                scope.launch { sheetState.show() }
                            },
                            onIncrement = foodViewModel::onIncrement,
                            onDecrement = foodViewModel::onDecrement,
                            onToggleLike = foodViewModel::toggleLike
                        )
                    }
                }
            }
        }
    }


}


@Composable
private fun SearchSuccessContent(
    uiState: SearchUiState.Success,
    quantities: Map<String, Int>,
    likedFoods: Map<String, Boolean>,
    onRestaurantClick: (String) -> Unit,
    onFoodClick: (Food) -> Unit,
    onIncrement: (Food, Int) -> Unit,
    onDecrement: (Food, Int) -> Unit,
    onToggleLike: (Food, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column {
                SectionTitle(title = "Foods")
                Spacer(Modifier.height(12.dp))
                if (uiState.foods.isEmpty()) {
                    EmptySectionText(text = "Food not found")
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(uiState.foods) { food ->
                            val currentQty = quantities[food.foodId] ?: 0
                            val isLiked = likedFoods[food.foodId] ?: false
                            Box(modifier = Modifier.width(160.dp)) {
                                FoodItem(
                                    food = food,
                                    quantity = currentQty,
                                    isLiked = isLiked,
                                    onFoodClicked = onFoodClick,
                                    onIncrement = { onIncrement(food, currentQty) },
                                    onDecrement = { onDecrement(food, currentQty) },
                                    onToggleLike = { onToggleLike(food, isLiked) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            SectionTitle(title = "Restaurants")
            Spacer(Modifier.height(12.dp))
            if (uiState.results.isEmpty()) {
                EmptySectionText(text = "Restaurant not found")
            }
        }

        items(uiState.results) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.name) }
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = SoftGold
    )
}

@Composable
private fun EmptySectionText(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        fontSize = 14.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchAction: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Pizza, Burgers...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Cancel, contentDescription = "Clear", tint = Color.Gray)
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SoftGold,
            unfocusedContainerColor = SoftGold,
            disabledContainerColor = SoftGold,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
        singleLine = true
    )
}

@Composable
fun SearchAnimationContent(
    animationRes: String,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset(animationRes))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )

        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SoftGold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = WarmWhite
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {


}
