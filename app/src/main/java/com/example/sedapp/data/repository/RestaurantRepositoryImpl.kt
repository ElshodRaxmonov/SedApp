package com.example.sedapp.data.repository

import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.domain.repository.RestaurantRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class RestaurantRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RestaurantRepository {

    override suspend fun getTopRestaurants(): List<Restaurant> {
        return firestore
            .collection("restaurants")
            .orderBy("rating", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                doc.toRestaurant()
            }
    }

    private fun DocumentSnapshot.toRestaurant(): Restaurant? {
        val name = getString("name") ?: return null
        val cuisine = getString("cuisine") ?: return null
        val location = getString("location") ?: ""
        val rating = getDouble("rating") ?: 0.0
        val images = get("images") as? List<String> ?: emptyList()

        return Restaurant(
            restaurantId = id,
            name = name,
            cuisine = cuisine,
            location = location,
            rating = rating,
            images = images
        )
    }
}

