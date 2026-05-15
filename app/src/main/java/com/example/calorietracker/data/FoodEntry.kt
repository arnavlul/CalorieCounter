package com.example.calorietracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val foodName: String,
    val multiplier: Double,
    val calories: Int,
    val carbs: Double,
    val proteins: Double,
    val fats: Double,
    val sugar: Double = 0.0,
    val date: LocalDate
)
