# Calorie Tracker Design Document

## Features
- **Food Input**: Manual entry of food name and calorie count.
- **Daily Storage**: Items are saved per date.
- **Horizontal Week View**: A scrolling calendar at the top to navigate between dates.
- **Running Sum**: Displayed at the bottom for the selected day.
- **Weekly Average**: Calculated based on the last 7 days of entries.

## Tech Stack
- **UI**: Jetpack Compose
- **Database**: Room (SQLite)
- **Concurrency**: Kotlin Coroutines & Flow
- **Dates**: `java.time.LocalDate`

## Data Model

### FoodEntry
- `id`: Int (Primary Key, Auto-increment)
- `name`: String
- `calories`: Int
- `date`: Long (Epoch days or Milli for Room storage)

## UI Layout
- **Top Bar**: "Calorie Tracker" title.
- **Horizontal Calendar**: A row of dates (Day of Week + Date) that are selectable.
- **Input Section**: Simple text fields for Name and Calories with an "Add" button.
- **Food List**: A scrollable list of food items for the selected date. Each item shows name and calories.
- **Bottom Bar**:
    - **Daily Total**: Big display of the sum of calories for the selected day.
    - **Weekly Average**: Smaller display of the average over the last 7 days.

## Implementation Steps
1. Add Room dependencies and KSP to `build.gradle.kts`.
2. Define `FoodEntry` entity, `FoodDao`, and `AppDatabase`.
3. Implement a Repository to handle data operations.
4. Create a `MainViewModel` to manage state (selected date, food list, totals).
5. Build the Compose UI components:
    - `HorizontalCalendar`
    - `FoodItemCard`
    - `FoodInputForm`
    - `SummaryFooter`
6. Integrate all components in `MainActivity`.
