package com.example.sedapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.sedapp.ui.navigation.AppNavHost
import com.example.sedapp.presentation.auth.screens.OnboardingScreen
import com.example.sedapp.ui.theme.SedAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SedAppTheme {
                val navController = rememberNavController()
                AppNavHost(navController, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    OnboardingScreen()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SedAppTheme {
        OnboardingScreen()

    }
}
