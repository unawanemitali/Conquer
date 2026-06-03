package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.models.*
import com.example.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val taskDao = db.taskDao()
    private val reviewDao = db.reviewDao()
    private val repository = TaskRepository(taskDao, reviewDao)

    // --- State Streams ---
    val tasksFlow: Flow<List<Task>> = repository.allTasksFlow
    val completionsFlow: Flow<List<TaskCompletion>> = repository.allCompletionsFlow
    val totalXpFlow: Flow<Int?> = repository.totalXpFlow

    // --- UI Filters and Configs ---
    private val _selectedCategoryFilter = MutableStateFlow<String?>(null) // null = "All"
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _calendarRangeType = MutableStateFlow(CalendarRangeType.WEEKLY)
    val calendarRangeType: StateFlow<CalendarRangeType> = _calendarRangeType.asStateFlow()

    private val _targetDateMillis = MutableStateFlow(System.currentTimeMillis())
    val targetDateMillis: StateFlow<Long> = _targetDateMillis.asStateFlow()

    private val _selectedCalendarDateMillis = MutableStateFlow(System.currentTimeMillis())
    val selectedCalendarDateMillis: StateFlow<Long> = _selectedCalendarDateMillis.asStateFlow()

    val allDailyNotesFlow: Flow<List<DailyNote>> = repository.allDailyNotesFlow

    val allReviewsFlow: Flow<List<Review>> = repository.allReviewsFlow

    fun saveReview(reviewType: String, wentWell: String, distractions: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertReview(
                Review(
                    reviewType = reviewType,
                    wentWell = wentWell,
                    distractions = distractions
                )
            )
        }
    }

    fun updateReview(id: Int, reviewType: String, createdAt: Long, wentWell: String, distractions: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertReview(
                Review(
                    id = id,
                    reviewType = reviewType,
                    createdAt = createdAt,
                    wentWell = wentWell,
                    distractions = distractions
                )
            )
        }
    }

    // Calculated metrics derived for calendar visualization
    private val _calendarResponse = MutableStateFlow<CalendarResponse?>(null)
    val calendarResponse: StateFlow<CalendarResponse?> = _calendarResponse.asStateFlow()

    // Streaks metric (Longest active and count)
    val activeStreaksCount: StateFlow<Int> = tasksFlow.map { tasks ->
        tasks.count { it.streak > 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val maxStreak: StateFlow<Int> = tasksFlow.map { tasks ->
        tasks.maxOfOrNull { it.streak } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Gamification level profiles
    val userLevelState: StateFlow<UserLevelInfo> = totalXpFlow.map { xp ->
        val safeXp = xp ?: 0
        val xpPerLevel = 200
        val level = (safeXp / xpPerLevel) + 1
        val progressXp = safeXp % xpPerLevel
        val fraction = progressXp.toFloat() / xpPerLevel.toFloat()
        
        UserLevelInfo(
            level = level,
            currentXp = safeXp,
            nextLevelXp = level * xpPerLevel,
            levelProgressPercent = fraction
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserLevelInfo())

    // --- Dynamic Milestone System Unlocks Flow ---
    val unlockedRewardsIdsFlow: StateFlow<Set<String>> = combine(
        totalXpFlow,
        completionsFlow,
        maxStreak
    ) { xp, completions, streak ->
        val safeXp = xp ?: 0
        val counts = mutableMapOf("Work" to 0, "Personal" to 0, "Others" to 0)
        completions?.forEach { comp ->
            val cat = comp.taskCategory
            if (cat == "Work" || cat == "Personal") {
                counts[cat] = (counts[cat] ?: 0) + 1
            } else {
                counts["Others"] = (counts["Others"] ?: 0) + 1
            }
        }
        
        MilestoneSystem.unlockablesList.filter { item ->
            MilestoneSystem.checkIsUnlocked(item, safeXp, counts, streak)
        }.map { it.id }.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // --- SharedPreferences Backed Equipped Avatar Skin ---
    private val sharedPrefs = application.getSharedPreferences("gamified_pref", Context.MODE_PRIVATE)
    
    private val _equippedSkinId = MutableStateFlow(sharedPrefs.getString("equipped_skin", "default") ?: "default")
    val equippedSkinId: StateFlow<String> = _equippedSkinId.asStateFlow()

    fun equipSkin(skinId: String) {
        sharedPrefs.edit().putString("equipped_skin", skinId).apply()
        _equippedSkinId.value = skinId
    }

    // --- Cosmetics Model, Configurations, JSON and State Streams ---
    data class CosmeticItem(
        val id: String,
        val name: String,
        val type: String, // "Skin" or "Theme"
        val priceXp: Int,
        val description: String,
        val iconEmoji: String,
        val aesthetic: String,
        val keyName: String
    )

    val cosmeticStoreItems = listOf(
        CosmeticItem("tokyo_runner", "Neo-Tokyo Runner", "Skin", 100, "Supercharged tactical cybergear with high-frequency head sensors.", "🧑‍🎤", "Cyberpunk", "tokyo_runner"),
        CosmeticItem("ghost_protocol", "Ghost Protocol Specs", "Skin", 500, "Active optical visual cloaking unit for zero-distraction focus loops.", "🥽", "Stealth Sci-Fi", "ghost_protocol"),
        CosmeticItem("chrono_weaver", "Chrono-Weaver Cloak", "Skin", 2000, "Hourglass node-harness that lets you perceive time flow as ticking particles.", "⌚", "Steampunk", "chrono_weaver"),
        CosmeticItem("mecha_pilot", "EVA Pilot Mark-X", "Skin", 5000, "Sleek carbon-weave suit for strategic neuro-synaptic resonance.", "🤖", "Mecha", "mecha_pilot"),
        CosmeticItem("star_marshal", "Hyperion Star-Marshal", "Skin", 10000, "Armored space-commander field suit powered by deep gravity generators.", "🚀", "Sci-Fi Knight", "star_marshal"),
        CosmeticItem("NET_RUNNER", "Net-Runner Deck", "Theme", 100, "A deep hacker-green visual interface inspired by digital matrix terminals.", "📟", "Retro Cyber", "NET_RUNNER"),
        CosmeticItem("LAB_DIVERTER", "Steins Laboratory", "Theme", 500, "Monochrome dark theme with radiant vacuum-tube orange signals.", "🧪", "Time Travel Sci-Fi", "LAB_DIVERTER"),
        CosmeticItem("DEEP_SPACE", "HAL-9000 Odyssey", "Theme", 2000, "Deep shipboard black background offset by alert crimson lights.", "👁️‍🗨️", "Sci-Fi Space", "DEEP_SPACE"),
        CosmeticItem("BRASS_ALCHEMY", "Equivalent Exchange", "Theme", 5000, "Rich academy navy blue background paired with glowing clockwork gold accents.", "⚙️", "Strategic Anime", "BRASS_ALCHEMY"),
        CosmeticItem("NEO_GENESIS", "Neo-Genesis Mecha", "Theme", 15000, "Evangelion purple and neon orange hues for cognitive synchronization.", "🪁", "Mecha Strategic", "NEO_GENESIS")
    )

    val COSMETIC_CONFIG_JSON = """
    [
      {
        "id": "tokyo_runner",
        "name": "Neo-Tokyo Runner",
        "type": "Skin",
        "priceXp": 100,
        "description": "Supercharged tactical cybergear with high-frequency head sensors.",
        "iconEmoji": "🧑‍🎤",
        "aesthetic": "Cyberpunk",
        "keyName": "tokyo_runner"
      },
      {
        "id": "ghost_protocol",
        "name": "Ghost Protocol Specs",
        "type": "Skin",
        "priceXp": 500,
        "description": "Active optical visual cloaking unit for zero-distraction focus loops.",
        "iconEmoji": "🥽",
        "aesthetic": "Stealth Sci-Fi",
        "keyName": "ghost_protocol"
      },
      {
        "id": "chrono_weaver",
        "name": "Chrono-Weaver Cloak",
        "type": "Skin",
        "priceXp": 2000,
        "description": "Hourglass node-harness that lets you perceive time flow as ticking particles.",
        "iconEmoji": "⌚",
        "aesthetic": "Steampunk",
        "keyName": "chrono_weaver"
      },
      {
        "id": "mecha_pilot",
        "name": "EVA Pilot Mark-X",
        "type": "Skin",
        "priceXp": 5000,
        "description": "Sleek carbon-weave suit for strategic neuro-synaptic resonance.",
        "iconEmoji": "🤖",
        "aesthetic": "Mecha",
        "keyName": "mecha_pilot"
      },
      {
        "id": "star_marshal",
        "name": "Hyperion Star-Marshal",
        "type": "Skin",
        "priceXp": 10000,
        "description": "Armored space-commander field suit powered by deep gravity generators.",
        "iconEmoji": "🚀",
        "aesthetic": "Sci-Fi Knight",
        "keyName": "star_marshal"
      },
      {
        "id": "NET_RUNNER",
        "name": "Net-Runner Deck",
        "type": "Theme",
        "priceXp": 100,
        "description": "A deep hacker-green visual interface inspired by digital matrix terminals.",
        "iconEmoji": "📟",
        "aesthetic": "Retro Cyber",
        "keyName": "NET_RUNNER"
      },
      {
        "id": "LAB_DIVERTER",
        "name": "Steins Laboratory",
        "type": "Theme",
        "priceXp": 500,
        "description": "Monochrome dark theme with radiant vacuum-tube orange signals.",
        "iconEmoji": "🧪",
        "aesthetic": "Time Travel Sci-Fi",
        "keyName": "LAB_DIVERTER"
      },
      {
        "id": "DEEP_SPACE",
        "name": "HAL-9000 Odyssey",
        "type": "Theme",
        "priceXp": 2000,
        "description": "Deep shipboard black background offset by alert crimson lights.",
        "iconEmoji": "👁️‍🗨️",
        "aesthetic": "Sci-Fi Space",
        "keyName": "DEEP_SPACE"
      },
      {
        "id": "BRASS_ALCHEMY",
        "name": "Equivalent Exchange",
        "type": "Theme",
        "priceXp": 5000,
        "description": "Rich academy navy blue background paired with glowing clockwork gold accents.",
        "iconEmoji": "⚙️",
        "aesthetic": "Strategic Anime",
        "keyName": "BRASS_ALCHEMY"
      },
      {
        "id": "NEO_GENESIS",
        "name": "Neo-Genesis Mecha",
        "type": "Theme",
        "priceXp": 15000,
        "description": "Evangelion purple and neon orange hues for cognitive synchronization.",
        "iconEmoji": "🪁",
        "aesthetic": "Mecha Strategic",
        "keyName": "NEO_GENESIS"
      }
    ]
    """.trimIndent()

    private val _unlockedCosmetics = MutableStateFlow<Set<String>>(
        sharedPrefs.getStringSet("unlocked_cosmetics_v2", emptySet()) ?: emptySet()
    )
    val unlockedCosmetics: StateFlow<Set<String>> = _unlockedCosmetics.asStateFlow()

    private val _equippedThemeId = MutableStateFlow(
        sharedPrefs.getString("equipped_theme", "MIDNIGHT_SYSTEM") ?: "MIDNIGHT_SYSTEM"
    )
    val equippedThemeId: StateFlow<String> = _equippedThemeId.asStateFlow()

    fun isCosmeticUnlocked(itemId: String): Boolean {
        if (itemId == "default" || itemId == "MIDNIGHT_SYSTEM") return true
        return _unlockedCosmetics.value.contains(itemId)
    }

    fun unlockCosmetic(itemId: String, priceXp: Int): Boolean {
        val currentXp = userLevelState.value.currentXp
        if (currentXp >= priceXp && !isCosmeticUnlocked(itemId)) {
            viewModelScope.launch {
                val completion = TaskCompletion(
                    taskId = -3000 - itemId.hashCode().coerceAtLeast(0),
                    taskTitle = "Unlocked Cosmetic: $itemId",
                    taskCategory = "Apothecary",
                    completedAt = System.currentTimeMillis(),
                    xpEarned = -priceXp
                )
                taskDao.insertCompletion(completion)

                val newUnlocked = _unlockedCosmetics.value + itemId
                sharedPrefs.edit().putStringSet("unlocked_cosmetics_v2", newUnlocked).apply()
                _unlockedCosmetics.value = newUnlocked
            }
            return true
        }
        return false
    }

    fun equipTheme(themeName: String) {
        val theme = try {
            AppThemePalette.valueOf(themeName)
        } catch (e: Exception) {
            AppThemePalette.MIDNIGHT_SYSTEM
        }
        AppThemeManager.updateTheme(getApplication(), theme)
        sharedPrefs.edit().putString("equipped_theme", themeName).apply()
        _equippedThemeId.value = themeName
    }

    // --- Apothecary Store & Potion Logic ---
    data class Potion(
        val id: String,
        val name: String,
        val description: String,
        val priceXp: Int,
        val iconEmoji: String,
        val hexColor: String
    )

    val apothecaryStorePotions = listOf(
        Potion("freeze_potion", "Freeze Potion", "Grace Day: Pauses streaks/XP decay for 24 hours", 50, "❄️", "#60A5FA"),
        Potion("overclock_elixir", "Overclock Elixir", "High risk/reward XP multiplier", 80, "⚡", "#F59E0B"),
        Potion("midnight_oil", "Midnight Oil", "Deadline extender with XP penalty", 50, "🛢️", "#10B981"),
        Potion("spartans_vow", "Spartan's Vow", "Focus lock on work tasks", 60, "🛡️", "#EF4444"),
        Potion("phoenix_tear", "Phoenix Tear", "Streak resurrection", 100, "🔥", "#EC4899"),
        Potion("amnesia_draft", "Amnesia Draft", "Guilt-free delete with reflection", 50, "🧪", "#8B5CF6")
    )

    fun getPotionPrice(potionId: String, basePrice: Int): Int {
        val count = sharedPrefs.getInt("potion_inflation_count_$potionId", 0)
        return (basePrice * Math.pow(2.5, count.toDouble())).toInt()
    }

    private val _potionCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val potionCounts: StateFlow<Map<String, Int>> = _potionCounts.asStateFlow()

    private val _activePotionEffects = MutableStateFlow<Map<String, Long>>(emptyMap())
    val activePotionEffects: StateFlow<Map<String, Long>> = _activePotionEffects.asStateFlow()

    private val _midnightOilTaskIds = MutableStateFlow<Set<Int>>(
        sharedPrefs.getStringSet("midnight_oil_task_ids", emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    )
    val midnightOilTaskIds: StateFlow<Set<Int>> = _midnightOilTaskIds.asStateFlow()

    fun applyMidnightOilToTask(taskId: Int): Boolean {
        var success = false
        val activeUntil = sharedPrefs.getLong("midnight_oil_active_until", 0L)
        val isEffectActive = activeUntil > System.currentTimeMillis()
        val count = sharedPrefs.getInt("potion_midnight_oil_count", 0)

        if (isEffectActive || count > 0) {
            viewModelScope.launch {
                val task = repository.getTaskById(taskId)
                if (task != null && !task.isCompleted) {
                    if (!isEffectActive && count > 0) {
                        // Consume 1 Midnight Oil potion
                        sharedPrefs.edit().putInt("potion_midnight_oil_count", count - 1).apply()
                    }
                    // Extend deadline by adding 24 hours (86400000 ms)
                    val newAssignedDate = task.assignedDateMillis + 86400000L
                    repository.updateTask(task.copy(assignedDateMillis = newAssignedDate))

                    val newSet = _midnightOilTaskIds.value + taskId
                    sharedPrefs.edit().putStringSet("midnight_oil_task_ids", newSet.map { it.toString() }.toSet()).apply()
                    _midnightOilTaskIds.value = newSet
                    loadPotionData()
                }
            }
            success = true
        }
        return success
    }

    fun loadPotionData() {
        val counts = mapOf(
            "freeze_potion" to sharedPrefs.getInt("potion_freeze_potion_count", 0),
            "overclock_elixir" to sharedPrefs.getInt("potion_overclock_elixir_count", 0),
            "midnight_oil" to sharedPrefs.getInt("potion_midnight_oil_count", 0),
            "spartans_vow" to sharedPrefs.getInt("potion_spartans_vow_count", 0),
            "phoenix_tear" to sharedPrefs.getInt("potion_phoenix_tear_count", 0),
            "amnesia_draft" to sharedPrefs.getInt("potion_amnesia_draft_count", 0)
        )
        _potionCounts.value = counts

        val effects = mapOf(
            "freeze_potion" to sharedPrefs.getLong("freeze_potion_active_until", 0L),
            "overclock_elixir" to sharedPrefs.getLong("overclock_elixir_active_until", 0L),
            "midnight_oil" to sharedPrefs.getLong("midnight_oil_active_until", 0L),
            "spartans_vow" to sharedPrefs.getLong("spartans_vow_active_until", 0L)
        )
        _activePotionEffects.value = effects
    }

    fun buyPotion(potion: Potion): Boolean {
        val currentPrice = getPotionPrice(potion.id, potion.priceXp)
        val currentXp = userLevelState.value.currentXp
        if (currentXp >= currentPrice) {
            viewModelScope.launch {
                val completion = TaskCompletion(
                    taskId = -1000 - potion.id.hashCode().coerceAtLeast(0),
                    taskTitle = "Bought Potion: ${potion.name}",
                    taskCategory = "Apothecary",
                    completedAt = System.currentTimeMillis(),
                    xpEarned = -currentPrice
                )
                taskDao.insertCompletion(completion)

                val key = "potion_${potion.id}_count"
                val currentCount = sharedPrefs.getInt(key, 0)
                sharedPrefs.edit().putInt(key, currentCount + 1).apply()

                // Increment inflation tracker upon buying
                val currentInflation = sharedPrefs.getInt("potion_inflation_count_${potion.id}", 0)
                sharedPrefs.edit().putInt("potion_inflation_count_${potion.id}", currentInflation + 1).apply()

                loadPotionData()
            }
            return true
        }
        return false
    }

    fun consumePotion(potion: Potion): Boolean {
        val key = "potion_${potion.id}_count"
        val currentCount = sharedPrefs.getInt(key, 0)
        if (currentCount > 0) {
            sharedPrefs.edit().putInt(key, currentCount - 1).apply()
            
            val duration = 86400000L // 24 hours
            val activeUntilKey = when(potion.id) {
                "freeze_potion" -> "freeze_potion_active_until"
                "overclock_elixir" -> "overclock_elixir_active_until"
                "midnight_oil" -> "midnight_oil_active_until"
                "spartans_vow" -> "spartans_vow_active_until"
                else -> null
            }
            if (activeUntilKey != null) {
                val currentActiveUntil = sharedPrefs.getLong(activeUntilKey, 0L)
                val baseTime = maxOf(System.currentTimeMillis(), currentActiveUntil)
                sharedPrefs.edit().putLong(activeUntilKey, baseTime + duration).apply()
            }

            // Increment inflation tracker upon using
            val currentInflation = sharedPrefs.getInt("potion_inflation_count_${potion.id}", 0)
            sharedPrefs.edit().putInt("potion_inflation_count_${potion.id}", currentInflation + 1).apply()

            viewModelScope.launch {
                val completion = TaskCompletion(
                    taskId = -2000 - potion.id.hashCode().coerceAtLeast(0),
                    taskTitle = "Consumed Potion: ${potion.name}",
                    taskCategory = "Apothecary",
                    completedAt = System.currentTimeMillis(),
                    xpEarned = 0
                )
                taskDao.insertCompletion(completion)
                loadPotionData()
            }
            return true
        }
        return false
    }

    // --- Aesthetic Level-Up Event Emitter ---
    data class LevelUpEvent(val oldLevel: Int, val newLevel: Int, val xpEarned: Int)
    
    private val _levelUpEvent = MutableSharedFlow<LevelUpEvent>(extraBufferCapacity = 5)
    val levelUpEvent: SharedFlow<LevelUpEvent> = _levelUpEvent.asSharedFlow()

    init {
        initNewUserProfile()
        loadPotionData()
        
        // Collect conditions to automatically regenerate calendar view when target date, 
        // range type, or database changes are done!
        viewModelScope.launch {
            combine(targetDateMillis, calendarRangeType, tasksFlow, completionsFlow) { date, range, tasks, _ ->
                Triple(date, range, tasks.size)
            }.collect { (date, range, taskCount) ->
                val safeTaskCount = if (taskCount == 0) 5 else taskCount // Fallback divisor proxy
                val response = when (range) {
                    CalendarRangeType.DAILY -> repository.getDailyMetrics(date, safeTaskCount)
                    CalendarRangeType.WEEKLY -> repository.getWeeklyMetrics(date, safeTaskCount)
                    CalendarRangeType.MONTHLY -> repository.getMonthlyMetrics(date, safeTaskCount)
                    CalendarRangeType.YEARLY -> repository.getYearlyMetrics(date, safeTaskCount)
                }
                _calendarResponse.value = response
            }
        }
    }

    /**
     * Initializes a clean-slate user profile on first install or first launch:
     * 1. level = 1 (or 0)
     * 2. XP = 0
     * 3. unlockedSkins, unlockedBadges, habitList all strictly empty
     * 4. Clear all persistent states to guarantee this completely empty start state
     */
    fun initNewUserProfile() {
        viewModelScope.launch {
            val isInitialized = sharedPrefs.getBoolean("profile_initialized_v2", false)
            if (!isInitialized) {
                // Completely empty SQLite database persistent state on launch on IO dispatcher
                withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(getApplication()).clearAllTables()
                }
                
                // Clear any leftover preferences and write true first-launch state
                sharedPrefs.edit()
                    .clear()
                    .putString("equipped_skin", "default")
                    .putBoolean("profile_initialized_v2", true)
                    .apply()
                
                _equippedSkinId.value = "default"
            }
            loadPotionData()
        }
    }

    // --- Custom Categories State Flows ---
    private val _customCategories = MutableStateFlow<List<String>>(
        sharedPrefs.getStringSet("custom_categories", emptySet())?.toList()?.sorted() ?: emptyList()
    )
    val customCategories: StateFlow<List<String>> = _customCategories.asStateFlow()

    val allCategoriesFlow: StateFlow<List<String>> = _customCategories.map { custom ->
        listOf("Work", "Personal", "Others") + custom
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Work", "Personal", "Others"))

    fun addCustomCategory(category: String) {
        val trimmed = category.trim()
        if (trimmed.isEmpty()) return
        val currentSet = sharedPrefs.getStringSet("custom_categories", emptySet()) ?: emptySet()
        val newSet = currentSet + trimmed
        sharedPrefs.edit().putStringSet("custom_categories", newSet).apply()
        _customCategories.value = newSet.toList().sorted()
    }

    fun updateTaskQuadrant(task: Task, newQuadrant: String) {
        viewModelScope.launch {
            val updated = task.copy(priorityQuadrant = newQuadrant)
            repository.updateTask(updated)
        }
    }

    // --- Action Handlers ---
    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setCalendarRangeType(type: CalendarRangeType) {
        _calendarRangeType.value = type
    }

    fun setTargetDate(dateMillis: Long) {
        _targetDateMillis.value = dateMillis
        _selectedCalendarDateMillis.value = dateMillis // Keep selected day in sync
    }

    fun setSelectedCalendarDate(dateMillis: Long) {
        _selectedCalendarDateMillis.value = dateMillis
    }

    fun addTask(
        title: String, 
        category: String, 
        isRecurring: Boolean, 
        xp: Int = 10, 
        difficulty: String = "Medium",
        assignedDateMillis: Long = System.currentTimeMillis(),
        frequencyDaysOfWeek: Int = 7,
        priorityQuadrant: String = "Not Urgent & Unimportant"
    ) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                category = category,
                isRecurring = isRecurring,
                xp = xp,
                difficulty = difficulty,
                assignedDateMillis = assignedDateMillis,
                frequencyDaysOfWeek = frequencyDaysOfWeek,
                priorityQuadrant = priorityQuadrant
            )
            repository.insertTask(newTask)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    suspend fun getDailyNoteForDate(dateStr: String): DailyNote? {
        return repository.getDailyNoteByDate(dateStr)
    }

    fun saveDailyNote(dateStr: String, noteText: String) {
        viewModelScope.launch {
            repository.insertDailyNote(DailyNote(dateStr = dateStr, note = noteText, lastUpdated = System.currentTimeMillis()))
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            if (task.isCompleted) {
                repository.undoCompleteTask(task)
            } else {
                // Get current total XP prior to completing
                val currentXp = totalXpFlow.first() ?: 0
                val xpPerLevel = 200
                val oldLevel = (currentXp / xpPerLevel) + 1
                
                // Complete task and get calculated XP (includes Category, Difficulty & Streak multipliers and Potion modifiers)
                val xpEarned = repository.completeTask(
                    task = task,
                    timestamp = System.currentTimeMillis(),
                    activePotions = _activePotionEffects.value,
                    midnightOilTaskIds = _midnightOilTaskIds.value
                )
                
                // Check if level increased
                val newXp = currentXp + xpEarned
                val newLevel = (newXp / xpPerLevel) + 1
                
                if (newLevel > oldLevel) {
                    _levelUpEvent.emit(LevelUpEvent(oldLevel = oldLevel, newLevel = newLevel, xpEarned = xpEarned))
                }
            }
        }
    }

    // --- Focus Timer Engine ---
    enum class TimerState { IDLE, RUNNING, PAUSED }

    private val _activeFocusTask = MutableStateFlow<Task?>(null)
    val activeFocusTask: StateFlow<Task?> = _activeFocusTask.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(1500) // 1500 secs = 25 mins
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _timerDurationTotal = MutableStateFlow(1500)
    val timerDurationTotal: StateFlow<Int> = _timerDurationTotal.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    val focusSessionsFlow: Flow<List<FocusSession>> = repository.allFocusSessionsFlow

    private var timerJob: kotlinx.coroutines.Job? = null

    fun selectTaskForFocus(task: Task?) {
        _activeFocusTask.value = task
    }

    fun startTimer(customMinutes: Int = 25) {
        timerJob?.cancel()
        val totalSecs = customMinutes * 60
        _timerDurationTotal.value = totalSecs
        _timerSecondsRemaining.value = totalSecs
        _timerState.value = TimerState.RUNNING

        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0) {
                kotlinx.coroutines.delay(1000)
                if (_timerState.value == TimerState.RUNNING) {
                    _timerSecondsRemaining.value -= 1
                }
            }
            completeFocusSession()
        }
    }

    fun pauseTimer() {
        if (_timerState.value == TimerState.RUNNING) {
            _timerState.value = TimerState.PAUSED
        }
    }

    fun resumeTimer() {
        if (_timerState.value == TimerState.PAUSED) {
            _timerState.value = TimerState.RUNNING
        }
    }

    fun stopTimer(interrupted: Boolean = true) {
        timerJob?.cancel()
        val totalSecs = _timerDurationTotal.value
        val remaining = _timerSecondsRemaining.value
        val spent = totalSecs - remaining
        val completedAt = System.currentTimeMillis()

        if (spent > 5) {
            viewModelScope.launch {
                val status = if (interrupted) "Interrupted" else "Completed"
                val xpEarned = if (!interrupted) 25 else 0

                val session = FocusSession(
                    taskId = _activeFocusTask.value?.id ?: -1,
                    taskTitle = _activeFocusTask.value?.title ?: "Free Focus",
                    durationSeconds = spent,
                    completedAt = completedAt,
                    status = status,
                    xpEarned = xpEarned
                )
                repository.insertFocusSession(session)

                // Dispatch task completion if completed
                if (!interrupted) {
                    val focusBonusCompletion = TaskCompletion(
                        taskId = -999, // Special ID for Focus completed
                        taskTitle = "Focus Done: " + (_activeFocusTask.value?.title ?: "Free Session"),
                        taskCategory = _activeFocusTask.value?.category ?: "Personal",
                        completedAt = completedAt,
                        xpEarned = xpEarned
                    )
                    taskDao.insertCompletion(focusBonusCompletion)

                    val currentXp = totalXpFlow.first() ?: 0
                    val xpPerLevel = 200
                    val oldLevel = (currentXp / xpPerLevel) + 1

                    // Auto-complete actual task/habit if selected
                    _activeFocusTask.value?.let { task ->
                        if (!task.isCompleted) {
                            toggleTaskCompletion(task)
                        }
                    }

                    // Check level-up from Focus Session itself
                    val newXp = currentXp + xpEarned
                    val newLevel = (newXp / xpPerLevel) + 1
                    if (newLevel > oldLevel) {
                        _levelUpEvent.emit(LevelUpEvent(oldLevel = oldLevel, newLevel = newLevel, xpEarned = xpEarned))
                    }
                }
            }
        }

        // Reset
        _timerState.value = TimerState.IDLE
        _activeFocusTask.value = null
        _timerSecondsRemaining.value = 1500
    }

    private fun completeFocusSession() {
        stopTimer(interrupted = false)
    }

    // --- "Eat the Frog" (Most Important Task) ---
    private fun getTodayDateStr(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }

    private val _frogTaskId = MutableStateFlow(sharedPrefs.getInt("eat_the_frog_task_id_${getTodayDateStr()}", 0))
    val frogTaskId: StateFlow<Int> = _frogTaskId.asStateFlow()

    fun tagEatTheFrog(taskId: Int) {
        val todayStr = getTodayDateStr()
        val currentTag = _frogTaskId.value
        if (currentTag == taskId) {
            sharedPrefs.edit().remove("eat_the_frog_task_id_$todayStr").apply()
            _frogTaskId.value = 0
        } else {
            sharedPrefs.edit().putInt("eat_the_frog_task_id_$todayStr", taskId).apply()
            _frogTaskId.value = taskId
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            if (_frogTaskId.value == task.id) {
                val todayStr = getTodayDateStr()
                sharedPrefs.edit().remove("eat_the_frog_task_id_$todayStr").apply()
                _frogTaskId.value = 0
            }
            repository.deleteTask(task)
        }
    }
}

/**
 * Visual packaging of user level details.
 */
data class UserLevelInfo(
    val level: Int = 1,
    val currentXp: Int = 0,
    val nextLevelXp: Int = 200,
    val levelProgressPercent: Float = 0f
)
