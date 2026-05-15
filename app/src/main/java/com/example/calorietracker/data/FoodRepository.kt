package com.example.calorietracker.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FoodRepository(private val foodDao: FoodDao) {
    // Food Entries
    fun getEntriesByDate(date: LocalDate): Flow<List<FoodEntry>> =
        foodDao.getEntriesByDate(date)

    fun getEntriesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<FoodEntry>> =
        foodDao.getEntriesBetweenDates(startDate, endDate)

    suspend fun insertEntry(entry: FoodEntry) {
        foodDao.insertEntry(entry)
    }

    suspend fun updateEntry(entry: FoodEntry) {
        foodDao.updateEntry(entry)
    }

    suspend fun deleteEntry(entry: FoodEntry) {
        foodDao.deleteEntry(entry)
    }

    suspend fun getEntriesByNameSince(name: String, sinceDate: LocalDate): List<FoodEntry> =
        foodDao.getEntriesByNameSince(name, sinceDate)

    suspend fun updateEntries(entries: List<FoodEntry>) {
        foodDao.updateEntries(entries)
    }

    fun getDailyTotals(): Flow<List<DailyTotal>> = foodDao.getDailyTotals()

    // Saved Foods
    val allSavedFoods: Flow<List<SavedFood>> = foodDao.getAllSavedFoods()

    suspend fun insertSavedFood(food: SavedFood) {
        foodDao.insertSavedFood(food)
    }

    suspend fun updateSavedFood(food: SavedFood) {
        foodDao.updateSavedFood(food)
    }

    suspend fun deleteSavedFood(food: SavedFood) {
        foodDao.deleteSavedFood(food)
    }
}
