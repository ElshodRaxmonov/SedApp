package com.example.sedapp.presentation.dashboard.home.restaurants

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.presentation.dashboard.component.FoodItem
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar
import com.example.sedapp.presentation.dashboard.home.foods.FoodDetailsSheet
import com.example.sedapp.presentation.dashboard.home.foods.FoodItemState
import com.example.sedapp.presentation.dashboard.home.foods.FoodViewModel
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun RestaurantDetailsScreenPreview() {

    val state = RestaurantDetailsUiState(
        isLoading = false,
        errorMessage = null,
        restaurant = Restaurant(
            "Restaurant 1",
            "Location 1",
            4.5,
            "Astana",
            cuisine = "Central Asian",
            listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
            description = "This is a restaurant description"
        ),
        foods = listOf(
            FoodItemState(
                Food(
                    "1",
                    "Burger",
                    "Delicious burger",
                    price = 12.0,
                    image = "",
                    isHalal = true,
                    category = "meal",
                    time = 12,
                    rating = 3.8,
                    restaurant = "Astana"
                ),
                quantity = 1
            )
        ),
    )

    RestaurantDetailsScreenContent(
        state = state,
        onBagClicked = {},
        onBackClicked = {},
        onIncrement = { _, _ -> },
        onDecrement = { _, _ -> },
        onAddToCart = { _, _ -> },
        onToggleLike = { _, _ -> }
    )
}

@Composable
fun RestaurantDetailsScreen(
    viewModel: RestaurantViewModel = hiltViewModel(),
    foodViewModel: FoodViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onBagClicked: () -> Unit
) {

    val state by viewModel.detailsState.collectAsStateWithLifecycle()

    RestaurantDetailsScreenContent(
        state = state,
        onBackClicked = onBackClicked,
        onBagClicked = onBagClicked,
        onIncrement = foodViewModel::onIncrement,
        onDecrement = foodViewModel::onDecrement,
        onAddToCart = foodViewModel::addToBag,
        onToggleLike = foodViewModel::toggleLike
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailsScreenContent(
    state: RestaurantDetailsUiState,
    onBagClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onIncrement: (Food, Int) -> Unit,
    onDecrement: (Food, Int) -> Unit,
    onAddToCart: (Food, Int) -> Unit,
    onToggleLike: (Food, Boolean) -> Unit
) {
    val restaurant = state.restaurant
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedFoodState by remember { mutableStateOf<FoodItemState?>(null) }
    var showMore by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SedAppTopBar(
                title = restaurant?.name ?: "",
                onBackClicked = onBackClicked
            )
        }
    ) { paddingValues ->
        if (restaurant == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Restaurant not found")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                val items = restaurant.images
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(221.dp)
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    elevation = CardDefaults.cardElevation(10.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftGold)
                ) {
                    if (items.isNotEmpty()) {
                        HorizontalMultiBrowseCarousel(
                            state = rememberCarouselState { items.size },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(221.dp)
                                .padding(8.dp),
                            preferredItemWidth = 186.dp,
                            itemSpacing = 6.dp,
                            minSmallItemWidth = 24.dp,
                            maxSmallItemWidth = 170.dp
                        ) { i ->
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .maskClip(MaterialTheme.shapes.extraLarge),
                                model = items[i],
                                placeholder = painterResource(R.drawable.meal),
                                error = painterResource(R.drawable.meal),
                                contentDescription = "Restaurant Image",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.rating_star),
                            contentDescription = "rating",
                            tint = SedAppOrange
                        )
                        Text(
                            " ${restaurant.rating}",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                        Spacer(Modifier.width(16.dp))
                        Icon(
                            painter = painterResource(R.drawable.cuisine),
                            null,
                            tint = SedAppOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            " ${restaurant.cuisine}",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.location),
                            null,
                            tint = SedAppOrange,
                            modifier = Modifier
                                .size(16.dp)

                        )
                        Text(
                            " ${restaurant.location}",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    Text(
                        restaurant.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Column(
                        modifier = Modifier
                            .animateContentSize(animationSpec = tween(100))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showMore = !showMore }) {

                        // if showMore is true, the Text will expand
                        // Else Text will be restricted to 3 Lines of display
                        if (showMore) {
                            Text(
                                text = restaurant.description, modifier =
                                    Modifier.padding(horizontal = 8.dp),
                                fontSize = 16.sp
                            )
                        } else {
                            Text(
                                text = restaurant.description,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis,
                                modifier =
                                    Modifier.padding(horizontal = 8.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            val groupedFoods = state.foods.groupBy { it.food.category }
            groupedFoods.forEach { (category, foodsInCategory) ->
                item {
                    Text(
                        category,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(foodsInCategory.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { itemState ->
                            Box(Modifier.weight(1f)) {
                                FoodItem(
                                    food = itemState.food,
                                    quantity = itemState.quantity,
                                    isLiked = itemState.isLiked,
                                    onFoodClicked = {
                                        selectedFoodState = itemState
                                        scope.launch { sheetState.show() }
                                    },
                                    onIncrement = {
                                        onIncrement(
                                            itemState.food,
                                            itemState.quantity
                                        )
                                    },
                                    onDecrement = {
                                        onDecrement(
                                            itemState.food,
                                            itemState.quantity
                                        )
                                    },
                                    onToggleLike = {
                                        onToggleLike(
                                            itemState.food,
                                            itemState.isLiked
                                        )
                                    }
                                )
                            }
                        }
                        if (rowItems.size < 2) Spacer(Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (selectedFoodState != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedFoodState = null }, sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                containerColor = Charcoal
            ) {
                FoodDetailsSheet(
                    food = selectedFoodState!!.food,
                    isLiked = selectedFoodState!!.isLiked,
                    onToggleLike = {
                        onToggleLike(
                            selectedFoodState!!.food,
                            selectedFoodState!!.isLiked
                        )
                    },
                    onAddToCart = { qty ->
                        onAddToCart(selectedFoodState!!.food, qty)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            selectedFoodState = null
                        }
                    },
                    onRestaurantClick = {
                        selectedFoodState = null
                    }
                )
            }
        }
    }
}
