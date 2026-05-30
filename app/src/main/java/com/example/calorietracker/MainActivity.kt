package com.example.calorietracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.calorietracker.data.InitialData
import com.example.calorietracker.data.AppDatabase
import com.example.calorietracker.data.FoodEntry
import com.example.calorietracker.data.FoodRepository
import com.example.calorietracker.data.SavedFood
import com.example.calorietracker.data.SettingsRepository
import com.example.calorietracker.ui.theme.CalorieTrackerTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

enum class Screen { Diary, Library }

class MainActivity : ComponentActivity() {
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { FoodRepository(database.foodDao()) }
    private val settingsRepository by lazy { SettingsRepository(this) }
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(repository, settingsRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch(Dispatchers.IO) {
            InitialData.populateDatabase(database.foodDao())
        }

        enableEdgeToEdge()
        setContent {
            val themeName by viewModel.themeName.collectAsState()
            CalorieTrackerTheme(themeName = themeName) {
                MainContainer(viewModel)
            }
        }
    }
}

@Composable
fun MainContainer(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.Diary) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == Screen.Diary,
                    onClick = { currentScreen = Screen.Diary },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Diary") },
                    label = { Text("Diary") }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Library,
                    onClick = { currentScreen = Screen.Library },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Library") },
                    label = { Text("Library") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            when (currentScreen) {
                Screen.Diary -> DiaryScreen(viewModel)
                Screen.Library -> LibraryScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(viewModel: MainViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val foodEntries by viewModel.foodEntries.collectAsState()
    val savedFoods by viewModel.savedFoods.collectAsState()
    val dailyTotals by viewModel.dailyTotals.collectAsState()
    val weeklyAvg by viewModel.weeklyAverageCalories.collectAsState()
    val allDailyTotals by viewModel.allDailyTotals.collectAsState()
    val dailyLimit by viewModel.dailyCalorieLimit.collectAsState()

    var showLogDialog by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<FoodEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<FoodEntry?>(null) }
    var showFullCalendar by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Diary") },
                actions = {
                    IconButton(onClick = { viewModel.selectDate(LocalDate.now()) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Today")
                    }
                    IconButton(onClick = { showFullCalendar = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Full Calendar")
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showLogDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Log Food")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.refreshData()
                    delay(500)
                    isRefreshing = false
                }
            },
            modifier = Modifier.padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalCalendar(
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.selectDate(it) },
                    dailyTotals = allDailyTotals,
                    dailyLimit = dailyLimit
                )

                SummaryHeader(dailyTotals, weeklyAvg)

                if (showFullCalendar) {
                    FullCalendarDialog(
                        initialDate = selectedDate,
                        dailyTotals = allDailyTotals,
                        dailyLimit = dailyLimit,
                        onDateSelected = { 
                            viewModel.selectDate(it)
                            showFullCalendar = false
                        },
                        onDismiss = { showFullCalendar = false }
                    )
                }

                val themeName by viewModel.themeName.collectAsState()
                if (showSettingsDialog) {
                    SettingsDialog(
                        currentLimit = dailyLimit,
                        currentTheme = themeName,
                        onConfirm = { limit, theme ->
                            viewModel.updateDailyCalorieLimit(limit)
                            viewModel.updateThemeName(theme)
                            showSettingsDialog = false
                        },
                        onDismiss = { showSettingsDialog = false }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(foodEntries) { entry ->
                        LogEntryCard(
                            entry, 
                            onEdit = { entryToEdit = it },
                            onDelete = { entryToDelete = it }
                        )
                    }
                }
            }
        }
    }

    if (showLogDialog) {
        LogFoodDialog(
            viewModel = viewModel,
            onDismiss = { showLogDialog = false },
            onConfirm = { food, mult ->
                viewModel.logFood(food, mult)
                showLogDialog = false
            }
        )
    }

    if (entryToEdit != null) {
        EditLogEntryDialog(
            entry = entryToEdit!!,
            savedFoods = savedFoods,
            onDismiss = { entryToEdit = null },
            onConfirm = { updatedEntry ->
                viewModel.updateEntry(updatedEntry)
                entryToEdit = null
            }
        )
    }

    if (entryToDelete != null) {
        DeleteEntryConfirmationDialog(
            entry = entryToDelete!!,
            onDismiss = { entryToDelete = null },
            onConfirm = {
                viewModel.deleteEntry(entryToDelete!!)
                entryToDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(viewModel: MainViewModel) {
    val savedFoods by viewModel.savedFoods.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var foodToEdit by remember { mutableStateOf<SavedFood?>(null) }
    var foodToDelete by remember { mutableStateOf<SavedFood?>(null) }
    val expandedNodes = remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(Unit) {
        viewModel.setSearchQuery("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Library") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add to Library")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search food items...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (searchQuery.isEmpty()) {
                    val rootNodes = savedFoods.filter { it.parentId == null }
                        .sortedWith(compareBy({ it.isCategory }, { it.name }))
                    rootNodes.forEach { node ->
                        foodTreeItems(
                            node = node,
                            allFoods = savedFoods,
                            level = 0,
                            expandedNodes = expandedNodes,
                            onEdit = { foodToEdit = it },
                            onDelete = { foodToDelete = it }
                        )
                    }
                } else {
                    val filteredFoods = savedFoods.filter { 
                        it.name.contains(searchQuery, ignoreCase = true)
                    }
                    val rootMatches = filteredFoods.filter { match ->
                        var parent = savedFoods.find { it.id == match.parentId }
                        var ancestorMatched = false
                        while (parent != null) {
                            if (filteredFoods.any { it.id == parent!!.id }) {
                                ancestorMatched = true
                                break
                            }
                            parent = savedFoods.find { it.id == parent!!.parentId }
                        }
                        !ancestorMatched
                    }
                    rootMatches.forEach { food ->
                        foodTreeItems(
                            node = food,
                            allFoods = savedFoods,
                            level = 0,
                            expandedNodes = expandedNodes,
                            onEdit = { foodToEdit = it },
                            onDelete = { foodToDelete = it }
                        )
                    }
                }
            }
        }
    }

    if (foodToDelete != null) {
        DeleteConfirmationDialog(
            food = foodToDelete!!,
            onDismiss = { foodToDelete = null },
            onConfirm = {
                viewModel.deleteSavedFood(foodToDelete!!)
                foodToDelete = null
            }
        )
    }

    if (showAddDialog || foodToEdit != null) {
        key(foodToEdit?.id) {
            AddSavedFoodDialog(
                savedFoods = savedFoods,
                foodToEdit = foodToEdit,
                onDismiss = { 
                    showAddDialog = false
                    foodToEdit = null
                },
                onConfirm = { name, cal, carb, prot, fat, sugar, portion, parentId, isCategory ->
                    if (foodToEdit != null) {
                        viewModel.updateSavedFood(foodToEdit!!, foodToEdit!!.copy(
                            name = name,
                            baseCalories = cal,
                            baseCarbs = carb,
                            baseProteins = prot,
                            baseFats = fat,
                            baseSugar = sugar,
                            portionSize = portion,
                            parentId = parentId,
                            isCategory = isCategory
                        ))
                    } else {
                        viewModel.saveFoodToLibrary(name, cal, carb, prot, fat, sugar, portion, parentId, isCategory)
                    }
                    showAddDialog = false
                    foodToEdit = null
                }
            )
        }
    }
}

fun androidx.compose.foundation.lazy.LazyListScope.foodTreeItems(
    node: SavedFood,
    allFoods: List<SavedFood>,
    level: Int,
    expandedNodes: MutableState<Set<Int>>,
    onEdit: (SavedFood) -> Unit,
    onDelete: (SavedFood) -> Unit
) {
    val isExpanded = expandedNodes.value.contains(node.id)
    
    item(key = "food_${node.id}_${node.parentId ?: 0}_${level}") {
        LibraryFoodCard(
            food = node,
            level = level,
            isExpanded = isExpanded,
            onToggleExpand = {
                if (isExpanded) {
                    expandedNodes.value -= node.id
                } else {
                    expandedNodes.value += node.id
                }
            },
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
    
    if (node.isCategory && isExpanded) {
        val children = allFoods.filter { it.parentId == node.id }
        children.forEach { child ->
            foodTreeItems(child, allFoods, level + 1, expandedNodes, onEdit, onDelete)
        }
    }
}

// --- Components ---

@Composable
fun SummaryHeader(totals: DailyTotals, weeklyAvg: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Calories", style = MaterialTheme.typography.labelMedium)
                    Text("${totals.calories} kcal", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("7-Day Avg", style = MaterialTheme.typography.labelMedium)
                    Text("${String.format("%.0f", weeklyAvg)} kcal", style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroSummaryItem("Carbs", totals.carbs)
                MacroSummaryItem("Protein", totals.proteins)
                MacroSummaryItem("Fats", totals.fats)
                MacroSummaryItem("Sugar", totals.sugar)
            }
        }
    }
}

@Composable
fun MacroSummaryItem(label: String, value: Double) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text("${String.format("%.1f", value)}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LogEntryCard(
    entry: FoodEntry, 
    onEdit: (FoodEntry) -> Unit,
    onDelete: (FoodEntry) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.foodName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "C: ${String.format("%.1f", entry.carbs)}g | P: ${String.format("%.1f", entry.proteins)}g | F: ${String.format("%.1f", entry.fats)}g | S: ${String.format("%.1f", entry.sugar)}g",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text("${entry.calories} kcal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = { onEdit(entry) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = { onDelete(entry) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun LibraryFoodCard(
    food: SavedFood, 
    level: Int = 0,
    isExpanded: Boolean = false,
    onToggleExpand: () -> Unit = {},
    onEdit: (SavedFood) -> Unit,
    onDelete: (SavedFood) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 16).dp)
            .clickable(enabled = food.isCategory) { onToggleExpand() },
        shape = RoundedCornerShape(8.dp),
        colors = if (food.isCategory) 
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) 
        else 
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (food.isCategory) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp).padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name, 
                    fontWeight = if (food.isCategory) FontWeight.Bold else FontWeight.Medium,
                    style = if (food.isCategory) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall
                )
                if (!food.isCategory) {
                    Text(
                        "${food.portionSize} | ${food.baseCalories} kcal | C:${food.baseCarbs}g P:${food.baseProteins}g F:${food.baseFats}g", 
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            IconButton(onClick = { onEdit(food) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = { onDelete(food) }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun LogFoodDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onConfirm: (SavedFood, Double) -> Unit
) {
    val savedFoods by viewModel.savedFoods.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredSavedFoods by viewModel.filteredSavedFoods.collectAsState()
    
    var navigationStack by remember { mutableStateOf(listOf<SavedFood>()) }
    var selectedFood by remember { mutableStateOf<SavedFood?>(null) }
    var multiplier by remember { mutableStateOf("1.0") }

    val isSearching = searchQuery.isNotEmpty()
    val currentItems = remember(navigationStack, savedFoods, isSearching, filteredSavedFoods) {
        if (isSearching) {
            filteredSavedFoods.sortedBy { it.name }
        } else {
            val parentId = navigationStack.lastOrNull()?.id
            savedFoods.filter { it.parentId == parentId }
                .sortedWith(compareBy({ it.isCategory }, { it.name }))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setSearchQuery("")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Log Food")
                if (!isSearching && navigationStack.isNotEmpty()) {
                    Text(
                        text = "Path: Root > " + navigationStack.joinToString(" > ") { it.name },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search all food...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (isSearching) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (!isSearching && navigationStack.isNotEmpty()) {
                    TextButton(
                        onClick = { 
                            navigationStack = navigationStack.dropLast(1)
                            selectedFood = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Back")
                    }
                }

                Box(modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth()) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(currentItems) { item ->
                            val isSelected = selectedFood?.id == item.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (item.isCategory && !isSearching) {
                                            navigationStack = navigationStack + item
                                            selectedFood = null
                                        } else {
                                            selectedFood = item
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (item.isCategory) Icons.AutoMirrored.Filled.List else Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(item.name, fontWeight = if (item.isCategory) FontWeight.Bold else FontWeight.Normal)
                                        if (!item.isCategory) {
                                            Text("${item.portionSize} - ${item.baseCalories} kcal", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    if (item.isCategory && !isSearching) {
                                        Spacer(Modifier.weight(1f))
                                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = multiplier,
                    onValueChange = { multiplier = it },
                    label = { Text("Multiplier (Quantity)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedFood != null && !selectedFood!!.isCategory) {
                    Text(
                        text = "Logging: ${multiplier.ifEmpty { "0" }} x ${selectedFood!!.portionSize}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedFood?.let { onConfirm(it, multiplier.trim().toDoubleOrNull() ?: 1.0) }
                },
                enabled = selectedFood != null
            ) { Text("Log") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddSavedFoodDialog(
    savedFoods: List<SavedFood>,
    foodToEdit: SavedFood? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Double, Double, Double, Double, String, Int?, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(foodToEdit?.name ?: "") }
    var calories by remember { mutableStateOf(foodToEdit?.baseCalories?.toString() ?: "") }
    var carbs by remember { mutableStateOf(foodToEdit?.baseCarbs?.toString() ?: "") }
    var proteins by remember { mutableStateOf(foodToEdit?.baseProteins?.toString() ?: "") }
    var fats by remember { mutableStateOf(foodToEdit?.baseFats?.toString() ?: "") }
    var sugar by remember { mutableStateOf(foodToEdit?.baseSugar?.toString() ?: "") }
    var portionSize by remember { mutableStateOf(foodToEdit?.portionSize ?: "") }
    var parentId by remember { mutableStateOf(foodToEdit?.parentId) }
    var isCategory by remember { mutableStateOf(foodToEdit?.isCategory ?: false) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (foodToEdit != null) "Edit Item" else "Add to Library") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isCategory, onCheckedChange = { isCategory = it })
                    Text("This is a Category")
                }

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                
                if (!isCategory) {
                    OutlinedTextField(value = portionSize, onValueChange = { portionSize = it }, label = { Text("Initial Portion Size (e.g., 1 cup, 100g)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = proteins, onValueChange = { proteins = it }, label = { Text("Protein (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = fats, onValueChange = { fats = it }, label = { Text("Fat (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sugar, onValueChange = { sugar = it }, label = { Text("Sugar (g)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                }
                
                Box {
                    var navStack by remember { mutableStateOf(listOf<SavedFood>()) }
                    val currentSelectionItems = remember(navStack, savedFoods) {
                        val pid = navStack.lastOrNull()?.id
                        savedFoods.filter { it.isCategory && it.parentId == pid && it.id != foodToEdit?.id }
                    }

                    OutlinedTextField(
                        value = savedFoods.find { it.id == parentId }?.name ?: "No Parent (Top Level)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Parent Category (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                            }
                        }
                    )
                    
                    if (expanded) {
                        AlertDialog(
                            onDismissRequest = { expanded = false },
                            title = { Text("Select Parent Category") },
                            text = {
                                Column(modifier = Modifier.heightIn(max = 400.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (navStack.isNotEmpty()) {
                                            IconButton(onClick = { navStack = navStack.dropLast(1) }) {
                                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                            }
                                        }
                                        Text(
                                            text = navStack.lastOrNull()?.name ?: "Top Level",
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    HorizontalDivider()
                                    LazyColumn {
                                        if (navStack.isEmpty()) {
                                            item {
                                                ListItem(
                                                    headlineContent = { Text("No Parent (Top Level)") },
                                                    modifier = Modifier.clickable {
                                                        parentId = null
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        } else {
                                            item {
                                                ListItem(
                                                    headlineContent = { Text("Select '${navStack.last().name}' as Parent") },
                                                    modifier = Modifier.clickable {
                                                        parentId = navStack.last().id
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                        items(currentSelectionItems) { item ->
                                            ListItem(
                                                headlineContent = { Text(item.name) },
                                                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
                                                modifier = Modifier.clickable { navStack = navStack + item }
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {},
                            dismissButton = {
                                TextButton(onClick = { expanded = false }) { Text("Cancel") }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val parsedCalories = calories.trim().toDoubleOrNull()?.toInt() ?: (foodToEdit?.baseCalories ?: 0)
                onConfirm(
                    name.trim(),
                    parsedCalories,
                    carbs.trim().toDoubleOrNull() ?: (foodToEdit?.baseCarbs ?: 0.0),
                    proteins.trim().toDoubleOrNull() ?: (foodToEdit?.baseProteins ?: 0.0),
                    fats.trim().toDoubleOrNull() ?: (foodToEdit?.baseFats ?: 0.0),
                    sugar.trim().toDoubleOrNull() ?: (foodToEdit?.baseSugar ?: 0.0),
                    portionSize.trim(),
                    parentId,
                    isCategory
                )
            }) { Text(if (foodToEdit != null) "Update" else "Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditLogEntryDialog(
    entry: FoodEntry,
    savedFoods: List<SavedFood>,
    onDismiss: () -> Unit,
    onConfirm: (FoodEntry) -> Unit
) {
    var multiplier by remember { mutableStateOf(entry.multiplier.toString()) }
    val baseFood = savedFoods.find { it.name == entry.foodName } // Try to find original food to recalculate

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Entry: ${entry.foodName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = multiplier,
                    onValueChange = { multiplier = it },
                    label = { Text("Multiplier (Quantity)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                if (baseFood == null) {
                    Text(
                        "Original food not found in library. Only multiplier will be saved, macros might not update correctly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = multiplier.trim().toDoubleOrNull() ?: entry.multiplier
                    val updatedEntry = if (baseFood != null) {
                        entry.copy(
                            multiplier = m,
                            calories = (baseFood.baseCalories * m).toInt(),
                            carbs = baseFood.baseCarbs * m,
                            proteins = baseFood.baseProteins * m,
                            fats = baseFood.baseFats * m,
                            sugar = baseFood.baseSugar * m
                        )
                    } else {
                        entry.copy(multiplier = m)
                    }
                    onConfirm(updatedEntry)
                }
            ) { Text("Update") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SettingsDialog(
    currentLimit: Int,
    currentTheme: String,
    onConfirm: (Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var limitText by remember { mutableStateOf(currentLimit.toString()) }
    var selectedTheme by remember { mutableStateOf(currentTheme) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Calorie Limit
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Daily Calorie Limit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { if (it.all { char -> char.isDigit() }) limitText = it },
                        label = { Text("Limit (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider()

                // Section 2: Themes List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("App Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    
                    val themes = listOf(
                        Triple("classic", "Classic (Sage & Peach)", listOf(Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFAB47BC))),
                        Triple("monochrome", "Monochrome (Black & Grey)", listOf(Color(0xFF212121), Color(0xFF757575), Color(0xFFE0E0E0))),
                        Triple("midnight", "Midnight (Navy & Slate)", listOf(Color(0xFF1A237E), Color(0xFF0D47A1), Color(0xFFBBDEFB))),
                        Triple("forest", "Forest (Green & Mint)", listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFFC8E6C9)))
                    )

                    themes.forEach { (themeId, themeLabel, colorList) ->
                        val isSelected = selectedTheme == themeId
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTheme = themeId },
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = themeLabel,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    colorList.forEach { color ->
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val limit = limitText.toIntOrNull() ?: currentLimit
                    onConfirm(limit, selectedTheme)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FullCalendarDialog(
    initialDate: LocalDate,
    dailyTotals: Map<LocalDate, Double>,
    dailyLimit: Int,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(initialDate.withDayOfMonth(1)) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.dayOfWeek.value % 7 // 0 for Sunday
    val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev")
                }
                Text(currentMonth.format(monthYearFormatter), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next")
                }
            }
        },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                        Text(it, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val totalSlots = daysInMonth + firstDayOfWeek
                val rows = (totalSlots + 6) / 7
                for (r in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        for (c in 0 until 7) {
                            val dayIndex = r * 7 + c - firstDayOfWeek + 1
                            if (dayIndex in 1..daysInMonth) {
                                val date = currentMonth.withDayOfMonth(dayIndex)
                                val calories = dailyTotals[date] ?: 0.0
                                val hasEntries = calories > 0
                                val isOverLimit = calories > dailyLimit
                                val isToday = date == LocalDate.now()
                                
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (date == initialDate) MaterialTheme.colorScheme.primary
                                            else if (isOverLimit) Color.Red.copy(alpha = 0.2f)
                                            else if (hasEntries) Color.Green.copy(alpha = 0.2f)
                                            else Color.Transparent
                                        )
                                        .border(
                                            width = if (isToday && date != initialDate) 1.dp else 0.dp,
                                            color = if (isToday && date != initialDate) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onDateSelected(date) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayIndex.toString(),
                                        color = if (date == initialDate) MaterialTheme.colorScheme.onPrimary
                                        else if (isOverLimit) Color.Red
                                        else if (hasEntries) Color(0xFF2E7D32) // Darker green for better readability
                                        else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isOverLimit || hasEntries || isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun HorizontalCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    dailyTotals: Map<LocalDate, Double>,
    dailyLimit: Int
) {
    val totalDays = 7300 // ~20 years
    val today = LocalDate.now()
    val startDate = today.minusDays(3650)
    val dates = remember { (0 until totalDays).map { startDate.plusDays(it.toLong()) } }
    val initialIndex = 3650
    
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex - 3)
    val scope = rememberCoroutineScope()

    // Scroll to selected date when it changes (e.g. from dialog or jump to today)
    LaunchedEffect(selectedDate) {
        val index = java.time.temporal.ChronoUnit.DAYS.between(startDate, selectedDate).toInt()
        if (index in 0 until totalDays) {
            // Only scroll if it's far from current view or specifically requested
            listState.animateScrollToItem(index - 3)
        }
    }

    val firstVisibleIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    val currentMonth = remember(firstVisibleIndex) {
        dates.getOrNull(firstVisibleIndex)?.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())) ?: ""
    }

    Column {
        Text(
            text = currentMonth,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(dates) { date ->
                DateItem(
                    date = date,
                    isSelected = date == selectedDate,
                    totalCalories = dailyTotals[date] ?: 0.0,
                    dailyLimit = dailyLimit,
                    onClick = { onDateSelected(date) }
                )
            }
        }
    }
}

@Composable
fun DateItem(date: LocalDate, isSelected: Boolean, totalCalories: Double, dailyLimit: Int, onClick: () -> Unit) {
    val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())
    val dateFormatter = DateTimeFormatter.ofPattern("dd", Locale.getDefault())
    val isToday = date == LocalDate.now()
    val isOverLimit = totalCalories > dailyLimit

    Column(
        modifier = Modifier
            .width(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary 
                else if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isToday && !isSelected) 2.dp else 0.dp,
                color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.format(dayFormatter), 
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, 
            fontSize = 11.sp
        )
        Text(
            text = date.format(dateFormatter), 
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Calorie Indicator
        if (totalCalories > 0) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(
                        when {
                            isOverLimit -> Color.Red
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            else -> Color(0xFF2E7D32) // Green
                        }
                    )
            )
        } else {
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    food: SavedFood,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete ${if (food.isCategory) "Category" else "Item"}?") },
        text = {
            Text(
                if (food.isCategory) 
                    "Are you sure you want to delete the category '${food.name}'? This will also delete all items and sub-categories inside it. This action cannot be undone."
                else 
                    "Are you sure you want to delete '${food.name}' from your library?"
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DeleteEntryConfirmationDialog(
    entry: FoodEntry,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Log Entry?") },
        text = {
            Text("Are you sure you want to delete '${entry.foodName}' from your diary?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
