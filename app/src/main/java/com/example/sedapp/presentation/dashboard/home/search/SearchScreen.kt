package com.example.sedapp.presentation.dashboard.search

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import com.example.sedapp.core.ui.theme.PrimaryOrange
import com.example.sedapp.presentation.dashboard.home.search.SearchUiState
import com.example.sedapp.presentation.dashboard.home.search.SearchViewModel

// File: ui/search/SearchScreen.kt
@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(onBackClicked = {})
}


@Composable
fun SearchScreen(

    viewModel: SearchViewModel = hiltViewModel(),
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // 1. Header Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.background(Color(0xFFF0F2F5), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(18.dp))
            }

            Text("Search", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

            IconButton(
                onClick = { /* Open Saved */ },
                modifier = Modifier.background(Color(0xFF1B2130), CircleShape)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = "Saved", tint = Color.White)
            }
        }

        Spacer(Modifier.height(24.dp))

        // 2. Search Input Field
        SearchTextField(
            value = viewModel.searchQuery,
            onValueChange = viewModel::onQueryChange,
            onSearchAction = viewModel::performSearch
        )

        Spacer(Modifier.height(40.dp))

        // 3. Dynamic Content Area
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            when (uiState) {
                is SearchUiState.Loading -> CircularProgressIndicator(color = PrimaryOrange)
                is SearchUiState.NotFound -> NotFoundContent()
                is SearchUiState.Success -> { /* List of results */ }
                else -> { /* Initial search prompts */ }
            }
        }
    }
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
        placeholder = { Text("Pizza", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Cancel, contentDescription = "Clear", tint = Color.LightGray)
                }
            }
        },
        keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF6F6F6),
            unfocusedContainerColor = Color(0xFFF6F6F6),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun NotFoundContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Lottie Animation
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("raw/not_found_animation.js"))
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
            text = "Not Found",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}