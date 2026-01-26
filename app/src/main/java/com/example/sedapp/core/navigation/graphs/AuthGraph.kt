package com.example.sedapp.core.navigation.graphs

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.sedapp.R
import com.example.sedapp.core.navigation.Routes
import com.example.sedapp.presentation.auth.AuthViewModel
import com.example.sedapp.presentation.auth.screens.SignInScreen
import com.example.sedapp.presentation.auth.screens.SignUpScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch


fun NavGraphBuilder.authGraph(
    navController: NavController
) {
    navigation(
        route = Routes.AUTH_GRAPH,
        startDestination = Routes.SIGN_IN
    ) {
        composable(Routes.SIGN_IN) {
            val viewModel: AuthViewModel = hiltViewModel()
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id)) // Requires google-services.json and build
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        account?.idToken?.let { token ->
                            viewModel.signInWithGoogle(token)
                        }
                    } catch (e: ApiException) {
                        e.printStackTrace()
                    }
                }
            }

            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Routes.DASHBOARD_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                    }
                },
                onSignUpClicked = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.SIGN_IN) {
                            inclusive = true
                        }
                    }
                },
                onGoogleSignInClick = {
                    launcher.launch(googleSignInClient.signInIntent)
                },
                viewModel = viewModel
            )
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Routes.DASHBOARD_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                    }
                },
                onBackClicked = {
                    navController.navigate(Routes.ONBOARDING_GRAPH) {
                        popUpTo(Routes.SIGN_UP) { inclusive = true }
                    }
                },
                viewModel = hiltViewModel()
            )
        }
    }
}
