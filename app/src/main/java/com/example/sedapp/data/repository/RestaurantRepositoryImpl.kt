package com.example.sedapp.data.repository

import com.example.sedapp.domain.model.Food
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

    override suspend fun getRestaurantFoods(restaurantName: String): List<Food> {

        return firestore.collection("foods").whereEqualTo("restaurant", restaurantName).get()
            .await()
            .documents.mapNotNull { doc ->
                doc.toFoodObject()
            }
    }

    override suspend fun getRestaurantDetails(restaurantName: String): Restaurant {
        return firestore.collection("restaurants")
            .whereEqualTo("name", restaurantName)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toRestaurant() ?: throw Exception("Restaurant not found")
    }

    override suspend fun getCuisines(): Set<String> {

        val cuisines = mutableSetOf<String>()
        firestore.collection("restaurants").get().await().documents.forEach {
            it.getString("cuisine")?.let { cuisine ->
                cuisines.add(cuisine)
            }
        }
        return cuisines
    }

    override suspend fun getSearchedFoods(query: String): List<Food> {
        // Industry Standard for simple Firestore prefix search: 
        // Using orderBy and startAt/endAt with the Unicode character \uf8ff
        return firestore.collection("foods")
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
            .documents
            .mapNotNull { doc -> doc.toFoodObject() }
    }

    override suspend fun getSearchedRestaurants(query: String): List<Restaurant> {
        return firestore.collection("restaurants")
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
            .documents
            .mapNotNull { doc -> doc.toRestaurant() }
    }

    private fun DocumentSnapshot.toRestaurant(): Restaurant? {
        val name = getString("name") ?: return null
        val cuisine = getString("cuisine") ?: return null
        val location = getString("location") ?: ""
        val rating = getDouble("rating") ?: 0.0
        val images = get("images") as? List<String> ?: emptyList()
        val description = getString("description") ?: ""

        return Restaurant(
            restaurantId = id,
            name = name,
            cuisine = cuisine,
            location = location,
            rating = rating,
            images = images,
            description = description
        )
    }

    fun DocumentSnapshot.toFoodObject(): Food {

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