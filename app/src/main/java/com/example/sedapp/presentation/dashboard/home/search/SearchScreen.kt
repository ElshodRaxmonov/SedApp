package com.example.sedapp.presentation.dashboard.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.domain.model.Food
import com.example.sedapp.presentation.dashboard.component.FoodItem
import com.example.sedapp.presentation.dashboard.home.RestaurantCard
import com.example.sedapp.presentation.dashboard.home.foods.FoodDetailsSheet
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
    val focusManager = LocalFocusManager.current
    var selectedFood by remember { mutableStateOf<Food?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            SearchTopBar(onBackClicked = onBackClicked)
        },
        containerColor = Color.White
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
                            animationRes = "raw/search_initial.json",
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
                            onRestaurantClick = onRestaurantClick,
                            onFoodClick = { food ->
                                selectedFood = food
                                scope.launch { sheetState.show() }
                            }
                        )
                    }
                }
            }
        }
    }

    if (selectedFood != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedFood = null },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(Color.LightGray, RoundedCornerShape(2.dp))
                )
            }
        ) {
            FoodDetailsSheet(
                food = selectedFood!!,
                onAddToCart = { qty ->
                    foodViewModel.addToBag(selectedFood!!, qty)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        selectedFood = null
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(onBackClicked: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Search",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(Color(0xFFF0F2F5), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Black
                )
            }
        },
        actions = {
            IconButton(
                onClick = { /* Open Saved */ },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .background(Color(0xFF1B2130), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    Icons.Default.BookmarkBorder,
                    contentDescription = "Saved",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
private fun SearchSuccessContent(
    uiState: SearchUiState.Success,
    onRestaurantClick: (String) -> Unit,
    onFoodClick: (Food) -> Unit
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
                            Box(modifier = Modifier.width(160.dp)) {
                                FoodItem(food = food, onFoodClicked = onFoodClick)
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
        color = Color.Black
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
                    Icon(Icons.Default.Cancel, contentDescription = "Clear", tint = Color.LightGray)
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF6F6F6),
            unfocusedContainerColor = Color(0xFFF6F6F6),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = SedAppOrange
        )
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
            modifier = Modifier.size(280.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}
