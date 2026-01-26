package com.example.sedapp.domain.usecase.bag

import com.example.sedapp.domain.repository.BagRepository
import javax.inject.Inject

class ClearBagUseCase @Inject constructor(
    private val repository: BagRepository
) {
    suspend operator fun invoke() {
        repository.clearBag()
    }
}
