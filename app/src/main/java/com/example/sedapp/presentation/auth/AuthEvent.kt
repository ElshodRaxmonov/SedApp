package com.example.sedapp.presentation.auth

sealed class AuthEvent {

    data class SignInWithEmailAndPassword(val email: String, val password: String) : AuthEvent()
    data class SignUpWithEmailAndPassword(
        val name: String,
        val email: String,
        val password: String
    ) : AuthEvent()

}