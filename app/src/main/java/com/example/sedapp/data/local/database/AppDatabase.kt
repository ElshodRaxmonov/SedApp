package com.example.sedapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sedapp.data.local.dao.FoodDao
import com.example.sedapp.data.local.entity.FoodEntity

@Database(entities = [FoodEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val foodDao: FoodDao
}
