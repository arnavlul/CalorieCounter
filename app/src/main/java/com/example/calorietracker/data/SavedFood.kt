package com.example.calorietracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_foods")
data class SavedFood(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val baseCalories: Int = 0,
    val baseCarbs: Double = 0.0,
    val baseProteins: Double = 0.0,
    val baseFats: Double = 0.0,
    val baseSugar: Double = 0.0,
    val portionSize: String = "",
    val parentId: Int? = null,
    val isCategory: Boolean = false
)
