package com.example.sedapp.presentation.dashboard.profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.DeepOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.domain.model.User
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar

@Preview
@Composable
fun PreviewProfile() {
    val user = User(
        name = "Rakhmonov Elshod",
        email = "rahmonovelshod42@gmail.com",
        uid = "cdscsdcsdcs"
    )
    ProfileScreenContent(user)
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    logOut: () -> Unit = {}

) {
    val user by viewModel.state.collectAsStateWithLifecycle()
    ProfileScreenContent(
        user, scrollState = state, modifier = modifier,
        logOut = {
            viewModel.signOut()
            logOut()
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    state: User?,
    scrollState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
    logOut: () -> Unit = {}
) {

    Scaffold(
        topBar = { SedAppTopBar(title = "Profile") }

    )
    { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Charcoal)
                    .padding(24.dp, vertical = 12.dp)
            ) {

                ProfileHeader(
                    name = state?.name ?: "Guest User",
                    email = state?.email ?: "No email linked"
                )

                Spacer(Modifier.height(32.dp))

                // Menu Sections
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        MenuCard(
                            listOf(
                                MenuItem(
                                    Icons.Default.PersonOutline,
                                    "Personal Info",
                                    Color(0xFFFFEBEB)
                                ),
                                MenuItem(Icons.Default.Map, "Addresses", Color(0xFFE8F1FF))
                            )
                        )
                    }
                    item {
                        MenuCard(
                            listOf(
                                MenuItem(Icons.Default.ShoppingBag, "Cart", Color(0xFFE8F1FF)),
                                MenuItem(
                                    Icons.Default.FavoriteBorder,
                                    "Favourite",
                                    Color(0xFFF5E8FF)
                                ),
                                MenuItem(
                                    Icons.Default.NotificationsNone,
                                    "Notifications",
                                    Color(0xFFE8FFFF)
                                ),
                                MenuItem(
                                    Icons.Default.CreditCard,
                                    "Payment Method",
                                    Color(0xFFE8F1FF)
                                )
                            )
                        )
                    }
                    item {
                        MenuCard(
                            listOf(
                                MenuItem(
                                    Icons.Default.HelpOutline, "FAQs",
                                    Color(0xFFFFEBEB)
                                ),
                                MenuItem(
                                    Icons.Default.RateReview,
                                    "User Reviews",
                                    Color(0xFFE8FFFF)
                                ),
                                MenuItem(Icons.Default.Settings, "Settings", Color(0xFFE8F1FF))
                            )
                        )
                    }
                    item {
                        LogoutCard(
                            userLoggedOut = {
                                logOut()
                            }

                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar with soft peach background
        Card(
            modifier = Modifier
                .size(100.dp)
                .border(2.dp, Color.White, CircleShape)
                .background(SoftGold, CircleShape),
            elevation = CardDefaults.cardElevation(16.dp),
            shape = CircleShape
        ) {

            Image(
                painter = painterResource(id = R.drawable.profile_avatar),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(),
                alignment = Alignment.Center,
                contentScale = androidx.compose.ui.layout.ContentScale.Inside

            )
        }

        Column(Modifier.padding(start = 16.dp)) {
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(email, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun MenuCard(items: List<MenuItem>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(item.iconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            item.icon,
                            null,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        item.label,
                        Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    androidx.compose.material3.Icon(
                        Icons.Default.ChevronRight,
                        null,
                        tint = DeepOrange
                    )
                }
            }
        }
    }
}

@Composable
fun LogoutCard(
    userLoggedOut: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        onClick = userLoggedOut
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(WarmWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) { androidx.compose.material3.Icon(Icons.Default.Logout, null, tint = Color.Red) }
            Text(
                "Log Out", Modifier
                    .padding(start = 16.dp)
                    .weight(1f), color = Color.DarkGray
            )
            androidx.compose.material3.Icon(
                Icons.Default.ChevronRight,
                null,
                tint = DeepOrange
            )
        }
    }
}

data class MenuItem(val icon: ImageVector, val label: String, val iconBg: Color)
