package com.example.sedapp.data.auth

import com.example.sedapp.domain.auth.model.User
import com.example.sedapp.domain.auth.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAuthRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    AuthRepository {
    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User> {
        return runCatching {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
                ?: throw IllegalStateException("User is null after sign in")
            user.toUser()
        }.mapError { exception ->
            mapFirebaseException(exception)
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Result<User> {
        return runCatching {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
                ?: throw IllegalStateException("User is null after sign up")

            // Update user profile with name
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdates).await()

            // Send email verification
            user.sendEmailVerification().await()

            user.toUser()
        }.mapError { exception ->
            mapFirebaseException(exception)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user
                ?: throw IllegalStateException("User is null after Google sign in")
            user.toUser()
        }.mapError { exception ->
            mapFirebaseException(exception)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Unit // Explicitly return Unit
        }.mapError { exception ->
            mapFirebaseException(exception)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun observeAuthState(): Flow<User?> {
        return callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                val user = auth.currentUser
                if (user != null) {
                    try {
                        trySend(user.toUser())
                    } catch (e: Exception) {
                        // If user data is invalid, send null
                        trySend(null)
                    }
                } else {
                    trySend(null)
                }
            }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose {
                firebaseAuth.removeAuthStateListener(listener)
            }
        }
    }

    private fun com.google.firebase.auth.FirebaseUser.toUser(): User {
        val userEmail = email ?: throw IllegalStateException("User email is null")
        return User(uid, displayName, userEmail)
    }

    /**
     * Maps Firebase-specific exceptions to more user-friendly exceptions.
     * This keeps Firebase implementation details in the data layer.
     */
    private fun mapFirebaseException(exception: Throwable): Throwable {
        return when (exception) {
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> IllegalArgumentException("Invalid email format")
                    "ERROR_WRONG_PASSWORD" -> IllegalArgumentException("Incorrect password")
                    "ERROR_USER_NOT_FOUND" -> IllegalArgumentException("No account found with this email")
                    "ERROR_USER_DISABLED" -> IllegalStateException("This account has been disabled")
                    "ERROR_TOO_MANY_REQUESTS" -> IllegalStateException("Too many requests. Please try again later")
                    "ERROR_EMAIL_ALREADY_IN_USE" -> IllegalArgumentException("An account already exists with this email")
                    "ERROR_WEAK_PASSWORD" -> IllegalArgumentException("Password is too weak")
                    "ERROR_NETWORK_REQUEST_FAILED" -> IllegalStateException("Network error. Please check your connection")
                    else -> Exception("Authentication failed: ${exception.message}")
                }
            }

            else -> exception
        }
    }

    /**
     * Extension function to map errors in Result
     */
    private fun <T> Result<T>.mapError(transform: (Throwable) -> Throwable): Result<T> {
        return fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception -> Result.failure(transform(exception)) }
        )
    }
}