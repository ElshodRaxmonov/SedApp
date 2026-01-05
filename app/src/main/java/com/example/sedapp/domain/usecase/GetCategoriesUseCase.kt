package com.example.sedapp.domain.usecase

import com.example.sedapp.R
import com.example.sedapp.domain.model.Category
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(){

    operator fun invoke(): List<Category> {
        return listOf(
            Category(1, "Meal",false),
            Category(2, "Drink",false),
            Category(3, "Bake",false),
            Category(4, "Snack",false)
        )
    }
}
