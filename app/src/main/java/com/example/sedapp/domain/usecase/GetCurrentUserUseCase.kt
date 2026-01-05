package com.example.sedapp.domain.usecase

import com.example.sedapp.domain.model.User
import com.example.sedapp.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): User? = authRepository.getCurrentUser()
}


