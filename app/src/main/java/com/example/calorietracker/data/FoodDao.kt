package com.example.calorietracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FoodDao {
    // Food Entries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FoodEntry)

    @Update
    suspend fun updateEntry(entry: FoodEntry)

    @Delete
    suspend fun deleteEntry(entry: FoodEntry)

    @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY id DESC")
    fun getEntriesByDate(date: LocalDate): Flow<List<FoodEntry>>

    @Query("SELECT * FROM food_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getEntriesBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<FoodEntry>>

    // Saved Foods (Library)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedFood(food: SavedFood)

    @Update
    suspend fun updateSavedFood(food: SavedFood)

    @Delete
    suspend fun deleteSavedFood(food: SavedFood)

    @Query("SELECT * FROM food_entries WHERE foodName = :name AND date >= :sinceDate")
    suspend fun getEntriesByNameSince(name: String, sinceDate: LocalDate): List<FoodEntry>

    @Update
    suspend fun updateEntries(entries: List<FoodEntry>)

    @Query("SELECT * FROM saved_foods ORDER BY name ASC")
    fun getAllSavedFoods(): Flow<List<SavedFood>>

    @Query("SELECT date, SUM(calories) as totalCalories FROM food_entries GROUP BY date")
    fun getDailyTotals(): Flow<List<DailyTotal>>
}
