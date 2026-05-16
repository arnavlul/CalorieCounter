package com.example.calorietracker.data

object InitialData {
    val foods = listOf(
        // Categories
        SavedFood(name = "Home / Mess", isCategory = true),
        SavedFood(name = "Home", isCategory = true),
        SavedFood(name = "Restaurant", isCategory = true),
        SavedFood(name = "Street Food", isCategory = true),
        SavedFood(name = "Packaged", isCategory = true),
        SavedFood(name = "Mess", isCategory = true),
        SavedFood(name = "Home / Sweet Shop", isCategory = true),
        SavedFood(name = "Home / Street Food", isCategory = true),
        SavedFood(name = "Outside / Fast Food", isCategory = true),
        SavedFood(name = "Outside", isCategory = true),
        SavedFood(name = "Packaged Food", isCategory = true),

        // Items
        // Home / Mess
        FoodItemData("Homemade Roti", "Home / Mess", "1 roti", 110, 3.5, 19.0, 1.5, 0.5),
        FoodItemData("Rajma", "Home / Mess", "150ml serving", 185, 8.5, 26.0, 5.5, 3.5),

        // Home
        FoodItemData("Bharwa Baingan Roll", "Home", "1 roll", 215, 4.0, 17.5, 11.0, 3.0),
        FoodItemData("Cooked Rice", "Home", "1 cup", 135, 2.5, 27.5, 0.5, 0.0),
        FoodItemData("Chicken Breast (cooked)", "Home", "100g", 170, 30.0, 1.0, 4.0, 0.0),
        FoodItemData("Egg Bhurji", "Home", "1 egg equivalent", 90, 6.5, 1.5, 7.0, 0.5),
        FoodItemData("Boiled Egg", "Home", "1 egg", 75, 6.5, 0.5, 5.5, 0.0),
        FoodItemData("Paneer Bhurji", "Home", "250g paneer serving", 725, 45.0, 15.0, 57.5, 5.5),

        // Restaurant
        FoodItemData("Handi Chicken", "Restaurant", "1 piece + gravy", 130, 11.5, 3.0, 8.5, 1.5),
        FoodItemData("Blueberry Cheesecake", "Restaurant", "1 slice", 375, 6.5, 37.5, 23.0, 24.0),
        FoodItemData("Tandoori Chicken (plain)", "Restaurant", "Quarter", 360, 35.0, 3.5, 20.0, 0.5),
        FoodItemData("Tandoori Chicken (butter+cream coated)", "Restaurant", "Quarter", 500, 37.5, 4.5, 35.0, 2.0),
        FoodItemData("Grilled Chicken Salad", "Restaurant", "1 serving", 325, 32.5, 10.0, 13.0, 3.5),
        FoodItemData("Chicken Caesar Salad", "Restaurant", "1 serving", 650, 27.5, 30.0, 47.5, 5.5),

        // Street Food
        FoodItemData("Veg Steamed Momo", "Street Food", "1 momo", 42, 1.5, 6.0, 1.5, 0.5),
        FoodItemData("Chicken Steamed Momo", "Street Food", "1 momo", 50, 4.0, 5.0, 2.0, 0.5),
        FoodItemData("Momo Red Chutney", "Street Food", "1 tbsp", 27, 0.0, 3.5, 2.0, 1.5),
        FoodItemData("Mayo", "Street Food", "1 tbsp", 67, 0.0, 1.0, 7.5, 0.5),
        FoodItemData("Golgappa / Pani Puri", "Street Food", "1 piece", 35, 0.5, 6.0, 0.5, 1.5),
        FoodItemData("Matar Kulcha", "Street Food", "Full serving", 660, 23.0, 107.5, 18.5, 7.5),
        FoodItemData("Tandoori Chaap (dry)", "Street Food", "Half plate", 315, 25.0, 14.0, 17.0, 3.5),
        FoodItemData("Butter-Coated Momo", "Street Food", "1 piece", 95, 3.0, 10.0, 5.0, 2.0),
        FoodItemData("Chicken Roll", "Street Food", "1 roll", 475, 22.5, 37.5, 17.5, 5.5),
        FoodItemData("Egg Roll", "Street Food", "1 roll", 400, 14.0, 35.0, 18.5, 4.0),
        FoodItemData("Chaap Roll", "Street Food", "1 roll", 525, 20.0, 42.5, 26.5, 6.0),
        FoodItemData("Bhelpuri", "Street Food", "\u20b920 serving", 230, 4.0, 37.5, 7.5, 7.5),

        // Packaged
        FoodItemData("Takatak / Namkeen", "Packaged", "100g", 525, 10.0, 55.0, 34.0, 4.0),

        // Mess
        FoodItemData("Dum Aloo", "Mess", "150ml serving", 180, 3.0, 20.0, 10.0, 4.5),
        FoodItemData("Dal", "Mess", "150ml serving", 125, 6.5, 16.0, 3.5, 2.0),
        FoodItemData("Mix Veg Sabzi", "Mess", "150ml serving", 215, 5.0, 30.0, 11.0, 4.5),
        FoodItemData("Nutree Matar", "Mess", "150ml serving", 180, 13.0, 14.0, 7.5, 3.0),
        FoodItemData("Chole", "Mess", "150ml serving", 230, 10.0, 28.5, 8.5, 4.5),
        FoodItemData("Fried Rice", "Mess", "1 serving", 350, 7.0, 55.0, 11.5, 3.5),

        // Home / Sweet Shop
        FoodItemData("Besan Ladoo", "Home / Sweet Shop", "1 ladoo", 215, 4.0, 25.0, 11.0, 20.0),

        // Home / Street Food
        FoodItemData("Kala Chana Chaat", "Home / Street Food", "150ml bowl", 110, 6.5, 16.0, 2.5, 2.0),

        // Outside / Fast Food
        FoodItemData("Chicken Burger (plain)", "Outside / Fast Food", "1 burger", 365, 24.0, 35.0, 11.5, 5.5),

        // Outside
        FoodItemData("Asian Chicken Croissant Sandwich", "Outside", "1 sandwich", 575, 24.0, 37.5, 35.0, 8.5),

        // Packaged Food
        FoodItemData("Parle-G Biscuit", "Packaged Food", "1 biscuit", 36, 0.5, 5.75, 1.25, 2.0)
    )

    data class FoodItemData(
        val name: String,
        val categoryName: String,
        val portion: String,
        val calories: Int,
        val proteins: Double,
        val carbs: Double,
        val fats: Double,
        val sugar: Double
    )

    suspend fun populateDatabase(foodDao: FoodDao) {
        val existingFoods = foodDao.getSavedFoodsList()
        val categories = foods.filterIsInstance<SavedFood>().filter { it.isCategory }
        val categoryMap = mutableMapOf<String, Int>()

        categories.forEach { category ->
            val existing = existingFoods.find { it.name == category.name && it.isCategory }
            if (existing == null) {
                val id = foodDao.insertSavedFood(category)
                categoryMap[category.name] = id.toInt()
            } else {
                categoryMap[category.name] = existing.id
            }
        }

        val items = foods.filterIsInstance<FoodItemData>()
        items.forEach { item ->
            val categoryId = categoryMap[item.categoryName]
            val existing = existingFoods.find { it.name == item.name && !it.isCategory }
            
            if (existing == null) {
                foodDao.insertSavedFood(
                    SavedFood(
                        name = item.name,
                        baseCalories = item.calories,
                        baseProteins = item.proteins,
                        baseCarbs = item.carbs,
                        baseFats = item.fats,
                        baseSugar = item.sugar,
                        portionSize = item.portion,
                        parentId = categoryId,
                        isCategory = false
                    )
                )
            } else if (existing.portionSize.isEmpty()) {
                // Update existing item if portion is empty
                foodDao.updateSavedFood(
                    existing.copy(
                        baseCalories = item.calories,
                        baseProteins = item.proteins,
                        baseCarbs = item.carbs,
                        baseFats = item.fats,
                        baseSugar = item.sugar,
                        portionSize = item.portion,
                        parentId = categoryId
                    )
                )
            }
        }
    }
}
