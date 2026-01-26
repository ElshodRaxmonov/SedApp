package com.example.sedapp.presentation.dashboard.home.restaurants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar
import com.example.sedapp.presentation.dashboard.home.RestaurantCard

@Preview(showBackground = true)
@Composable
fun RestaurantScreenPreview() {

    val state = RestaurantUiState(
        isLoading = false,
        errorMessage = null,
        availableCuisines = listOf("Central Asian", "Turkey", "Chinese"),
        restaurants = listOf(
            Restaurant(
                "Restaurant 1",
                "Location 1",
                4.5,
                "Astana",
                cuisine = "Central Asian",
                listOf("Image URL 1", "Image URL 2"),
                description = "This is a description"
            ), Restaurant(
                "Restaurant 1",
                "Location 1",
                4.5,
                "Astana",
                cuisine = "Chinese",
                listOf("Image URL 1", "Image URL 2"),
                description = "This is a description"
            ), Restaurant(
                "Restaurant 1",
                "Location 1",
                4.5,
                "Astana",
                cuisine = "Turkey",
                listOf("Image URL 1", "Image URL 2"),
                description = "This is a description"
            )
        ),

        )

    RestaurantScreenContent(
        state = state,
        onBackClicked = {},
        onCuisineSelected = {},
        onRestaurantClick = {})
}

@Composable
fun RestaurantScreen(
    onBackClicked: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    viewModel: RestaurantViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    RestaurantScreenContent(
        onRestaurantClick = {
            viewModel.onRestaurantClick(it)
            onRestaurantClick(it)
        },
        onCuisineSelected = viewModel::onCuisineSelected,
        onBackClicked = onBackClicked,
        state = state
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantScreenContent(
    onRestaurantClick: (String) -> Unit,
    onCuisineSelected: (String) -> Unit,
    onBackClicked: () -> Unit,
    state: RestaurantUiState
) {

    Scaffold(
        topBar = {
            SedAppTopBar(
                title = "Restaurants", onBackClicked = onBackClicked
            )
        },
        containerColor = Charcoal
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Cuisines",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SoftGold
            )
            LazyRow(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.availableCuisines) { cuisine ->
                    val isSelected = cuisine in state.selectedCuisines
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCuisineSelected(cuisine) },
                        label = { Text(cuisine) },
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
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = SedAppOrange,
                            selected = false,
                            enabled = true
                        ),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SedAppOrange,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            labelColor = WarmWhite, disabledContainerColor = SedAppOrange

                        ))
                }
            }
            // Top Bar
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.restaurants) { restaurant ->
                    RestaurantCard(restaurant, onClick = { onRestaurantClick(restaurant.name) })
                }
            }
        }
    }
}