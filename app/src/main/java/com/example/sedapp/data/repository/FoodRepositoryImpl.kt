package com.example.sedapp.data.repository

import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.repository.FoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FoodRepository {

    override suspend fun getFoods(): List<Food> {
        return try {
            firestore.collection("foods").get().await().toObjects(Food::class.java)
        } catch (e: Exception) {
            // In a real app, you'd want to log this error
            emptyList()
        }
    }
}
