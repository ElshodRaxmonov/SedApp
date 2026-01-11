package com.example.sedapp.data.di

import com.example.sedapp.data.repository.BagRepositoryImpl
import com.example.sedapp.data.repository.FoodRepositoryImpl
import com.example.sedapp.data.repository.PreferenceRepositoryImpl
import com.example.sedapp.data.repository.RestaurantRepositoryImpl
import com.example.sedapp.domain.repository.BagRepository
import com.example.sedapp.domain.repository.FoodRepository
import com.example.sedapp.domain.repository.PreferencesRepository
import com.example.sedapp.domain.repository.RestaurantRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferenceRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(
        impl: RestaurantRepositoryImpl
    ): RestaurantRepository

    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        foodRepositoryImpl: FoodRepositoryImpl
    ): FoodRepository

    @Binds
    @Singleton
    abstract fun bindBagRepository(
        impl: BagRepositoryImpl
    ): BagRepository
}
