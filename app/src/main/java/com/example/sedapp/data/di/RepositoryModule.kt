package com.example.sedapp.data.di

import com.example.sedapp.data.repository.FoodRepositoryImpl
import com.example.sedapp.data.repository.PreferenceRepositoryImpl
import com.example.sedapp.data.repository.RestaurantRepositoryImpl
import com.example.sedapp.domain.repository.FoodRepository
import com.example.sedapp.domain.repository.PreferencesRepository
import com.example.sedapp.domain.repository.RestaurantRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPreferencesRepository(impl: PreferenceRepositoryImpl): PreferencesRepository

    @Binds
    abstract fun bindRestaurantRepository(
        impl: RestaurantRepositoryImpl
    ): RestaurantRepository

    @Binds
    abstract fun bindFoodRepository(
        foodRepositoryImpl: FoodRepositoryImpl
    ): FoodRepository
}