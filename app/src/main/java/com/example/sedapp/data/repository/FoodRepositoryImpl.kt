package com.example.sedapp.data.repository

import android.util.Log
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.repository.FoodRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FoodRepository {

    override suspend fun getFoods(): List<Food> {
        return try {
            firestore.collection("foods").get().await().documents.mapNotNull { doc ->
                doc.toFoodObject()
            }
        } catch (e: Exception) {

            Log.d("in usecase", "getFoods: ${e.message}")
            emptyList()
        }
    }

    public fun DocumentSnapshot.toFoodObject(): Food {

        return Food(
            name = getString("name") ?: "",
            description = getString("description") ?: "",
            price = getDouble("price") ?: 0.0,
            image = getString("image") ?: "",
            isHalal = getBoolean("isHalal") ?: false,
            category = getString("category") ?: "",
            time = getLong("time")?.toInt() ?: 0,
            rating = getDouble("rating") ?: 0.0,
            restaurant = getString("restaurant") ?: "",
            foodId = id
        )
    }
}
