package com.example.sedapp.presentation.dashboard.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    items: List<T>,
    initialIndex: Int,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 40.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snappingLayout = rememberSnapFlingBehavior(lazyListState = listState)

    // Reactive selection tracking
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { it % items.size }
            .distinctUntilChanged()
            .collect { index ->
                onItemSelected(items[index])
            }
    }

    Box(modifier = modifier.height(itemHeight * 3), contentAlignment = Alignment.Center) {
        // Selection Lens
        Column {
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.fillMaxWidth(0.8f))
            Spacer(Modifier.height(itemHeight))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.fillMaxWidth(0.8f))
        }

        LazyColumn(
            state = listState,
            flingBehavior = snappingLayout,
            contentPadding = PaddingValues(vertical = itemHeight),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(Int.MAX_VALUE) { index ->
                val item = items[index % items.size]
                val isSelected = listState.firstVisibleItemIndex == index
                
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.toString(),
                        fontSize = if (isSelected) 20.sp else 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun PickerDropUp(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Semi-transparent background that consumes clicks
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // The Picker Surface - consumes clicks inside to prevent dismissal
        Surface(
            modifier = modifier
                .padding(bottom = 100.dp)
                .widthIn(min = 180.dp, max = 300.dp) // Adjusted for better responsiveness
                .clickable(enabled = false) { }, 
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = title, 
                    color = Color(0xFFE76F00), 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}
