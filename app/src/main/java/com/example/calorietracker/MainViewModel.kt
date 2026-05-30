package com.example.calorietracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calorietracker.data.FoodEntry
import com.example.calorietracker.data.FoodRepository
import com.example.calorietracker.data.SavedFood
import com.example.calorietracker.data.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DailyTotals(
    val calories: Int = 0,
    val carbs: Double = 0.0,
    val proteins: Double = 0.0,
    val fats: Double = 0.0,
    val sugar: Double = 0.0
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: FoodRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val foodEntries: StateFlow<List<FoodEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getEntriesByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedFoods: StateFlow<List<SavedFood>> = repository.allSavedFoods
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredSavedFoods: StateFlow<List<SavedFood>> = combine(savedFoods, _searchQuery) { foods, query ->
        if (query.isBlank()) foods
        else foods.filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyTotals: StateFlow<DailyTotals> = foodEntries
        .map { entries ->
            DailyTotals(
                calories = entries.sumOf { it.calories },
                carbs = entries.sumOf { it.carbs },
                proteins = entries.sumOf { it.proteins },
                fats = entries.sumOf { it.fats },
                sugar = entries.sumOf { it.sugar }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyTotals())

    val allDailyTotals: StateFlow<Map<LocalDate, Double>> = repository.getDailyTotals()
        .map { list -> list.associate { it.date to it.totalCalories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val dailyCalorieLimit: StateFlow<Int> = settingsRepository.dailyCalorieLimit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), settingsRepository.getDailyCalorieLimitValue())

    val themeName: StateFlow<String> = settingsRepository.themeName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), settingsRepository.getThemeNameValue())

    val weeklyAverageCalories: StateFlow<Double> = _selectedDate
        .flatMapLatest { date ->
            // Past 7 days (excluding today as per "before the current day")
            val start = date.minusDays(7)
            val end = date.minusDays(1)
            repository.getEntriesBetweenDates(start, end)
        }
        .map { entries ->
            if (entries.isEmpty()) 0.0
            else {
                val dailyTotals = entries.groupBy { it.date }
                    .mapValues { (_, dayEntries) -> dayEntries.sumOf { it.calories } }
                
                // Average over only the days that actually have logs
                dailyTotals.values.sum().toDouble() / dailyTotals.size.toDouble()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun refreshData() {
        val currentDate = _selectedDate.value
        // Temporarily change date and back to trigger flatMapLatest refresh
        _selectedDate.value = currentDate.plusDays(1)
        _selectedDate.value = currentDate
    }

    // Food Entry Actions
    fun logFood(savedFood: SavedFood, multiplier: Double) {
        viewModelScope.launch {
            repository.insertEntry(
                FoodEntry(
                    foodName = savedFood.name,
                    multiplier = multiplier,
                    calories = (savedFood.baseCalories * multiplier).toInt(),
                    carbs = savedFood.baseCarbs * multiplier,
                    proteins = savedFood.baseProteins * multiplier,
                    fats = savedFood.baseFats * multiplier,
                    sugar = savedFood.baseSugar * multiplier,
                    date = _selectedDate.value
                )
            )
        }
    }

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun updateEntry(entry: FoodEntry) {
        viewModelScope.launch {
            repository.updateEntry(entry)
        }
    }

    // Saved Food Actions (Library)
    fun saveFoodToLibrary(
        name: String,
        calories: Int = 0,
        carbs: Double = 0.0,
        proteins: Double = 0.0,
        fats: Double = 0.0,
        sugar: Double = 0.0,
        portionSize: String = "",
        parentId: Int? = null,
        isCategory: Boolean = false
    ) {
        viewModelScope.launch {
            repository.insertSavedFood(
                SavedFood(
                    name = name,
                    baseCalories = calories,
                    baseCarbs = carbs,
                    baseProteins = proteins,
                    baseFats = fats,
                    baseSugar = sugar,
                    portionSize = portionSize,
                    parentId = parentId,
                    isCategory = isCategory
                )
            )
        }
    }

    fun updateSavedFood(oldFood: SavedFood, newFood: SavedFood) {
        viewModelScope.launch {
            repository.insertSavedFood(newFood)

            // Cascade changes to entries from the last 30 days
            val sinceDate = LocalDate.now().minusDays(30)
            val entriesToUpdate = repository.getEntriesByNameSince(oldFood.name, sinceDate)

            if (entriesToUpdate.isNotEmpty()) {
                val updatedEntries = entriesToUpdate.map { entry ->
                    entry.copy(
                        foodName = newFood.name,
                        calories = (newFood.baseCalories * entry.multiplier).toInt(),
                        carbs = newFood.baseCarbs * entry.multiplier,
                        proteins = newFood.baseProteins * entry.multiplier,
                        fats = newFood.baseFats * entry.multiplier,
                        sugar = newFood.baseSugar * entry.multiplier
                    )
                }
                repository.updateEntries(updatedEntries)
            }
        }
    }

    fun deleteSavedFood(savedFood: SavedFood) {
        viewModelScope.launch {
            repository.deleteSavedFood(savedFood)
        }
    }

    fun updateDailyCalorieLimit(limit: Int) {
        settingsRepository.setDailyCalorieLimit(limit)
    }

    fun updateThemeName(theme: String) {
        settingsRepository.setThemeName(theme)
    }
}

class MainViewModelFactory(
    private val repository: FoodRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
