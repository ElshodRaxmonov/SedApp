package com.example.sedapp.domain.repository

import com.example.sedapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User?

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>

    suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Result<User>

    suspend fun signInWithGoogle(idToken: String): Result<User>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun signOut()

    fun observeAuthState(): Flow<User?>
}