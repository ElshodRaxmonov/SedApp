package com.example.sedapp.presentation.dashboard.home.foods

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.core.ui.theme.White
import com.example.sedapp.core.util.getFormattedPrice
import com.example.sedapp.domain.model.Category
import com.example.sedapp.domain.model.Currency
import com.example.sedapp.domain.model.Food
import com.example.sedapp.presentation.dashboard.component.FoodItem
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar
import kotlinx.coroutines.launch


@Preview
@Composable
fun FoodScreenPreview() {
    val uiStateMock = FoodUiState(
        false, listOf(
            Category(1, "Meal", false),
            Category(2, "Drink", false),
            Category(3, "Bake", false),
            Category(4, "Snack", false)
        ), emptyList()
    )
    FoodScreenContent(
        uiState = uiStateMock,
        onBackClicked = {},
        onCategorySelected = {},
        onHalalFilterChanged = {},
        onAddToBag = { _, _ -> },
        onIncrement = { _, _ -> },
        onDecrement = { _, _ -> },
        onToggleLike = { _, _ -> },
        onRestaurantClick = {}
    )
}


@Composable
fun FoodScreen(
    onBackClicked: () -> Unit, viewModel: FoodViewModel = hiltViewModel(),
    restaurantClicked: (name: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    FoodScreenContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onCategorySelected = viewModel::onCategorySelected,
        onHalalFilterChanged = viewModel::onHalalFilterChanged,
        onAddToBag = viewModel::addToBag,
        onIncrement = viewModel::onIncrement,
        onDecrement = viewModel::onDecrement,
        onToggleLike = viewModel::toggleLike,
        onRestaurantClick = restaurantClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreenContent(
    uiState: FoodUiState,
    onBackClicked: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onHalalFilterChanged: (Boolean) -> Unit,
    onAddToBag: (Food, Int) -> Unit,
    onIncrement: (Food, Int) -> Unit,
    onDecrement: (Food, Int) -> Unit,
    onToggleLike: (Food, Boolean) -> Unit,
    onRestaurantClick: (name: String) -> Unit
) {
    var selectedFoodState by remember { mutableStateOf<FoodItemState?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SedAppTopBar(
                title = "Food",
                onBackClicked = onBackClicked
            )
        },
        containerColor = Charcoal
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.errorMessage)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SoftGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoriesSection(
                        categories = uiState.availableCategories,
                        selectedCategories = uiState.selectedCategoryNames,
                        onCategorySelected = onCategorySelected,
                        isHalalOnly = uiState.isHalalOnly,
                        onHalalFilterChanged = onHalalFilterChanged
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                val groupedFoods = uiState.displayedFoods.groupBy { it.food.category }
                groupedFoods.forEach { (category, foodsInCategory) ->
                    item {
                        Text(
                            category,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SoftGold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(foodsInCategory.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowItems.forEach { itemState ->
                                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
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
                            if (rowItems.size < 2) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
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
                        onAddToBag(selectedFoodState!!.food, qty)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            selectedFoodState = null
                        }
                    },
                    onRestaurantClick = {
                        onRestaurantClick(selectedFoodState!!.food.restaurant)
                    }
                )
            }
        }
    }
}

@Composable
fun CategoriesSection(
    categories: List<Category>,
    selectedCategories: Set<String>,
    onCategorySelected: (String) -> Unit,
    isHalalOnly: Boolean,
    onHalalFilterChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category.name in selectedCategories
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category.name) },
                    label = { Text(category.name) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                    border = BorderStroke(1.dp, SedAppOrange),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SedAppOrange,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White,
                        labelColor = WarmWhite,
                    )
                )
            }
        }

        Switch(
            checked = isHalalOnly,
            onCheckedChange = onHalalFilterChanged,
            modifier = Modifier.padding(start = 16.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = WarmWhite,
                checkedTrackColor = SedAppOrange,
                uncheckedThumbColor = WarmWhite,
                uncheckedTrackColor = SoftGold
            ),
            thumbContent = {
                Icon(
                    painter = painterResource(
                        if (isHalalOnly) R.drawable.halal else R.drawable.no_halal
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            })
    }
}

@Composable
fun FoodDetailsSheet(
    food: Food,
    isLiked: Boolean,
    onToggleLike: () -> Unit,
    onAddToCart: (Int) -> Unit,
    onRestaurantClick: (nameRestaurant: String) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var liked by remember { mutableStateOf(isLiked) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
                .height(200.dp)
                .clip(
                    RoundedCornerShape(24.dp)
                )
                .border(
                    width = 3.dp,
                    color = SedAppOrange,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            AsyncImage(
                model = food.image,
                contentDescription = food.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = {
                    onToggleLike()
                    liked = !liked
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    tint = if (liked) Color.Red else Color.Gray,
                    contentDescription = "Favorite"
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = food.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SoftGold
                )
                Card(
                    modifier = Modifier.padding(4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = WarmWhite,
                        contentColor = Color.Black,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.Black
                    ),
                    onClick = { onRestaurantClick(food.restaurant) },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.restaurant),
                            contentDescription = "Restaurant Icon",
                            modifier = Modifier.size(18.dp),
                            tint = SedAppOrange
                        )
                        Text(
                            text = food.restaurant,
                            modifier = Modifier.padding(start = 4.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rating_star),
                    contentDescription = "rating",
                    tint = SedAppOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = food.rating.toString(),
                    modifier = Modifier.padding(start = 4.dp),
                    color = White
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.halal_or_not),
                    contentDescription = "Halal",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = if (food.isHalal) {
                        "Halal"
                    } else {
                        "Not Halal"
                    }, modifier = Modifier.padding(start = 4.dp),
                    color = White
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ready_time),
                    contentDescription = "time",
                    tint = SedAppOrange,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${food.time} min", modifier = Modifier.padding(start = 4.dp),
                    color = White
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = food.description,
                fontSize = 14.sp,
                color = Color.LightGray,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = WarmWhite,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        getFormattedPrice(Currency.RM, food.price * quantity),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                Color.DarkGray, RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Remove",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = Color.White
                        )
                        IconButton(onClick = { quantity++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onAddToCart(quantity) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SedAppOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "ADD TO CART", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Bpreview() {

    FoodDetailsSheet(
        food = Food(
            "3",
            "Burger",
            "Delicious burger",
            10.0,
            "https://example.com/burger.jpg",
            true,
            "Drink",
            10,
            4.5,
            "Burger Bistro"
        ),
        isLiked = false,
        onToggleLike = {},
        onAddToCart = {},
        onRestaurantClick = {}
    )
}