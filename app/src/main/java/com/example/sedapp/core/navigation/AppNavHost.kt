package com.example.sedapp.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.sedapp.core.navigation.graphs.authGraph
import com.example.sedapp.core.navigation.graphs.dashboardGraph
import com.example.sedapp.core.navigation.graphs.onboardingGraph
import com.example.sedapp.domain.model.AppStartDestination
import com.example.sedapp.presentation.main.MainUiState
import com.example.sedapp.presentation.main.MainViewModel
import com.example.sedapp.presentation.splashscreen.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is MainUiState.Loading -> {
            SplashScreen()
        }
        is MainUiState.Ready -> {
            val startDestination = when (state.destination) {
                AppStartDestination.Onboarding -> Routes.ONBOARDING_GRAPH
                AppStartDestination.Auth -> Routes.AUTH_GRAPH
                AppStartDestination.Dashboard -> Routes.DASHBOARD_GRAPH
            }

            NavHost(
                modifier = modifier,
                navController = navController,
                startDestination = startDestination,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) }

            ) {
                onboardingGraph(navController)
                authGraph(navController)
                dashboardGraph(navController)
            }
        }
    }
}
