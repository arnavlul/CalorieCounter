package com.example.calorietracker.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("calorie_tracker_prefs", Context.MODE_PRIVATE)
    
    private val _dailyCalorieLimit = MutableStateFlow(prefs.getInt("daily_calorie_limit", 2000))
    val dailyCalorieLimit: Flow<Int> = _dailyCalorieLimit.asStateFlow()

    private val _themeName = MutableStateFlow(prefs.getString("theme_name", "classic") ?: "classic")
    val themeName: Flow<String> = _themeName.asStateFlow()

    fun setDailyCalorieLimit(limit: Int) {
        prefs.edit().putInt("daily_calorie_limit", limit).apply()
        _dailyCalorieLimit.value = limit
    }
    
    fun getDailyCalorieLimitValue(): Int {
        return _dailyCalorieLimit.value
    }

    fun setThemeName(theme: String) {
        prefs.edit().putString("theme_name", theme).apply()
        _themeName.value = theme
    }

    fun getThemeNameValue(): String {
        return _themeName.value
    }
}
