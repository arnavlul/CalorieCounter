# Calorie Tracker 🍏

A modern, offline-first Android application designed for precision nutrition tracking. Built with **Jetpack Compose** and **Room**, it offers a seamless experience for logging meals, managing a personal food library, and monitoring macro-nutrients.

## ✨ Features

- **📅 Dynamic Food Diary**: Log meals for any date using an intuitive horizontal calendar.
- **🍗 Hierarchical Food Library**: Organize foods into categories (e.g., "Breakfast," "Supplements") for quick access.
- **📊 Macro-Nutrient Tracking**: Monitor Calories, Carbs, Protein, Fats, and Sugar for every entry.
- **📉 Rolling Averages**: Stay on top of your goals with a 7-day rolling calorie average.
- **🎯 Goal Setting**: Define a custom daily calorie limit with visual indicators (Green/Red) in the calendar history.
- **⚡ Smart Sync**: Updating a food's nutrition in your library automatically updates all logs from the last 30 days.

## 🛠️ Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Database**: [Room SQLite](https://developer.android.com/training/data-storage/room)
- **Concurrency**: Kotlin Coroutines & Flow
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Dependency Management**: Gradle Version Catalogs
- **Language**: 100% Kotlin

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/arnavlul/CalorieCounter.git
   ```
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Sync Gradle and run the app on an emulator or physical device (API 24+).

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
