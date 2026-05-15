package com.example.calorietracker.data

import java.time.LocalDate

data class DailyTotal(
    val date: LocalDate,
    val totalCalories: Double
)
