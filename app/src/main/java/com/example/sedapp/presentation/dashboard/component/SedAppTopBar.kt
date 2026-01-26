package com.example.sedapp.presentation.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SedAppYellow
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.core.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SedAppTopBar(
    title: String,
    onBackClicked: (() -> Unit)? = null,
    showLogo: Boolean = true
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                color = White,
                modifier = Modifier.padding(start = if (onBackClicked == null) 24.dp else 0.dp)
            )
        },
        navigationIcon = {
            if (onBackClicked != null) {
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp)
                        .background(WarmWhite, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (showLogo) {
                Image(
                    painter = painterResource(id = R.drawable.sedapp_logo_grey),
                    contentDescription = "SedApp icon",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    colorFilter = ColorFilter.tint(SedAppYellow)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SedAppOrange),
        windowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp), // Reset default insets to handle them manually with statusBarsPadding
        modifier = Modifier
            .statusBarsPadding() // Ensures the TopBar starts below the status bar
            .padding(8.dp)
            .shadow(
                8.dp,
                RoundedCornerShape(16.dp),
                ambientColor = Color.Gray,
                spotColor = Color.Gray
            )
            .clip(
                RoundedCornerShape(32.dp)
            ),
    )
}
