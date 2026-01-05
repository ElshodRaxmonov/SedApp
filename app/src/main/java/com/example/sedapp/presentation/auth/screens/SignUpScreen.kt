package com.example.sedapp.presentation.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.sedapp.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sedapp.presentation.auth.AuthViewModel
import com.example.sedapp.presentation.auth.AuthUiState
import com.example.sedapp.core.ui.theme.BackgroundPale
import com.example.sedapp.core.ui.theme.DeepOrange
import com.example.sedapp.core.ui.theme.PrimaryOrange
import com.example.sedapp.core.ui.theme.TextFieldBackground

@Composable
fun SignUpScreen(
    onBackClicked: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {},
    viewModel: AuthViewModel
) {
    // State management for form inputs
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var retypePassword by remember { mutableStateOf("") }

    // State for password visibility toggles
    var passwordVisible by remember { mutableStateOf(false) }
    var retypePasswordVisible by remember { mutableStateOf(false) }
    
    val authState by viewModel.state.collectAsStateWithLifecycle()
    
    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthUiState.Success -> {
                onSignUpSuccess()
            }
            is AuthUiState.Error -> {
                // Error is shown via Snackbar below
            }
            else -> {}
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error snackbar
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (authState as AuthUiState.Error).message,
                duration = SnackbarDuration.Long
            )
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundPale,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Top Orange Header Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f) // Slightly smaller header than Sign In, based on the image proportion
                    .background(DeepOrange)
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            ) {
                FilledIconButton(
                    onClick = onBackClicked,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.padding(4.dp)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.decort_three),
                    contentDescription = "decort 3",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Sign Up",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Please sign up to get started",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                    )
                }
            }

            // 2. Main Content Area (White Card Look)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f)
                    .offset(y = (-40).dp) // Pull the content up over the curve
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Name Field

                CustomFlatTextField(
                    titleField = "NAME",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "john doe",
                    keyboardType = KeyboardType.Text
                )
                Spacer(modifier = Modifier.height(16.dp))


                CustomFlatTextField(
                    titleField = "EMAIL",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "example@gmail.com",
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Password Field
                CustomFlatTextField(
                    titleField = "PASSWORD",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        PasswordVisibilityToggle(passwordVisible) {
                            passwordVisible = it
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Re-type Password Field
                CustomFlatTextField(
                    titleField = "RE-TYPE PASSWORD",
                    value = retypePassword,
                    onValueChange = { retypePassword = it },
                    placeholder = "••••••••",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (retypePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        PasswordVisibilityToggle(retypePasswordVisible) {
                            retypePasswordVisible = it
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Sign Up Button
                val isLoading = authState is AuthUiState.Loading
                val passwordsMatch = password == retypePassword
                val isFormValid = name.isNotBlank() && email.isNotBlank() && 
                    password.isNotBlank() && retypePassword.isNotBlank() && passwordsMatch
                
                if (!passwordsMatch && retypePassword.isNotBlank()) {
                    Text(
                        text = "Passwords do not match",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
                
                Button(
                    onClick = { 
                        if (isFormValid && !isLoading) {
                            viewModel.signUpWithEmail(name, email, password)
                        }
                    },
                    enabled = isFormValid && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "SIGN UP",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// Custom TextField Composable to match the flat, rounded design
@Composable
fun CustomFlatTextField(
    titleField: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(
            text = titleField,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.6f)) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TextFieldBackground,
                unfocusedContainerColor = TextFieldBackground,
                disabledContainerColor = TextFieldBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryOrange,
                focusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// Password Visibility Toggle Icon Composable
@Composable
fun PasswordVisibilityToggle(isVisible: Boolean, onToggle: (Boolean) -> Unit) {
    val image = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
    val description = if (isVisible) "Hide password" else "Show password"

    IconButton(onClick = { onToggle(!isVisible) }) {
        Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
    }
}

// Reusable text style for field labels

@Preview(showBackground = true)
@Composable
fun PreviewSignUpScreen() {
    MaterialTheme {
//        SignUpScreen()
    }
}
// Custom