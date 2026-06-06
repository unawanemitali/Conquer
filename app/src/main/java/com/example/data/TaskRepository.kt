package com.example.data

import com.example.data.models.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class TaskRepository(
    private val taskDao: TaskDao,
    private val reviewDao: ReviewDao
) {

    // --- Reviews CRUD ---
    val allReviewsFlow: Flow<List<Review>> = reviewDao.getAllReviewsFlow()

    suspend fun insertReview(review: Review): Long = reviewDao.insertReview(review)

    // --- Task CRUD ---
    val allTasksFlow: Flow<List<Task>> = taskDao.getAllTasksFlow()
    val allCompletionsFlow: Flow<List<TaskCompletion>> = taskDao.getAllCompletionsFlow()
    val allFocusSessionsFlow: Flow<List<FocusSession>> = taskDao.getAllFocusSessionsFlow()
    val totalXpFlow: Flow<Int?> = taskDao.getTotalXpFlow()

    suspend fun getTaskById(id: Int): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) {
        taskDao.deleteCompletionsByTaskId(task.id)
        taskDao.deleteTask(task)
    }

    suspend fun insertFocusSession(session: FocusSession): Long = taskDao.insertFocusSession(session)

    // --- DailyNotes ---
    val allDailyNotesFlow: Flow<List<DailyNote>> = taskDao.getAllDailyNotesFlow()
    suspend fun getDailyNoteByDate(dateStr: String): DailyNote? = taskDao.getDailyNoteByDate(dateStr)
    suspend fun insertDailyNote(note: DailyNote) = taskDao.insertDailyNote(note)

    private fun getLogicalTodayString(timestamp: Long = System.currentTimeMillis()): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour < 4) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    /**
     * Mark a task as completed or uncompleted.
     * Completing a task logs a TaskCompletion entry in our time-series log,
     * awards XP, and increments streaks for recurring tasks.
     */
    suspend fun completeTask(
        task: Task, 
        timestamp: Long = System.currentTimeMillis(),
        activePotions: Map<String, Long> = emptyMap(),
        midnightOilTaskIds: Set<Int> = emptySet()
    ): Int {
        val todayStr = getLogicalTodayString(timestamp)
        val updatedHistory = if (task.history.contains(todayStr)) {
            task.history
        } else {
            task.history + todayStr
        }

        val updatedStreak = if (task.isRecurring) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val historySet = updatedHistory.toSet()
            
            var currentStreak = 0
            val cal = Calendar.getInstance()
            try {
                val todayDate = sdf.parse(todayStr)
                if (todayDate != null) {
                    cal.time = todayDate
                    while (true) {
                        val checkStr = sdf.format(cal.time)
                        if (historySet.contains(checkStr)) {
                            currentStreak++
                            cal.add(Calendar.DAY_OF_YEAR, -1)
                        } else {
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                currentStreak = task.streak + 1
            }
            maxOf(currentStreak, 1)
        } else {
            0
        }

        // Update task state in database
        taskDao.updateTaskCompletionState(task.id, true, updatedStreak, timestamp, todayStr, todayStr, updatedHistory)

        // Math-based Dynamic XP System Calculations
        val categoryFactor = when (task.category.lowercase(Locale.ROOT)) {
            "work" -> 1.2f
            "personal" -> 1.0f
            "others" -> 0.8f
            else -> 1.0f
        }
        val difficultyFactor = when (task.difficulty.lowercase(Locale.ROOT)) {
            "easy" -> 1.0f
            "medium" -> 1.5f
            "hard" -> 2.0f
            "mythic" -> 3.0f
            else -> 1.5f
        }
        val streakBonus = 1.0f + (if (task.isRecurring) minOf(updatedStreak, 10) * 0.05f else 0.0f)
        
        val baseXP = task.xp
        val finalXP = baseXP.toDouble()
        val xpEarned = Math.round(finalXP * difficultyFactor * categoryFactor * streakBonus).toInt()

        // Log completion in time-series database
        val completion = TaskCompletion(
            taskId = task.id,
            taskTitle = task.title,
            taskCategory = task.category,
            completedAt = timestamp,
            xpEarned = xpEarned,
            completedOnLogicalDate = todayStr
        )
        taskDao.insertCompletion(completion)
        return xpEarned
    }

    /**
     * Revert / Undo a completion for a task for the current day.
     */
    suspend fun undoCompleteTask(task: Task, timestamp: Long = System.currentTimeMillis()) {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val (startOfDay, endOfDay) = getDayRange(calendar)
        
        val todayStr = getLogicalTodayString(timestamp)
        val updatedHistory = task.history.filter { it != todayStr }
        taskDao.deleteCompletionForDay(task.id, startOfDay, endOfDay)

        val newStreak = if (task.isRecurring) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val sortedDates = updatedHistory.sorted()
            if (sortedDates.isNotEmpty()) {
                val lastDateStr = sortedDates.last()
                val historySet = updatedHistory.toSet()
                var currentStreak = 0
                val cal = Calendar.getInstance()
                try {
                    val lastDate = sdf.parse(lastDateStr)
                    if (lastDate != null) {
                        cal.time = lastDate
                        while (true) {
                            val checkStr = sdf.format(cal.time)
                            if (historySet.contains(checkStr)) {
                                currentStreak++
                                cal.add(Calendar.DAY_OF_YEAR, -1)
                            } else {
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    currentStreak = 0
                }
                currentStreak
            } else {
                0
            }
        } else {
            0
        }

        taskDao.updateTaskCompletionState(task.id, false, newStreak, 0L, "", "", updatedHistory)
    }

    // --- Optimized Time-Series Temporal Range Queries ---

    /**
     * Query 1: DAILY View Metrics
     * Groups task completions by Morning (00:00-11:59), Afternoon (12:00-17:59), and Evening (18:00-23:59).
     */
    suspend fun getDailyMetrics(targetDateMillis: Long, totalTasksCount: Int): CalendarResponse {
        val cal = Calendar.getInstance().apply { timeInMillis = targetDateMillis }
        val (startOfDay, endOfDay) = getDayRange(cal)

        // Optimized retrieve of entries in target range only
        val completions = taskDao.getCompletionsInRange(startOfDay, endOfDay)

        // Buckets
        var morningComp = 0
        var afternoonComp = 0
        var eveningComp = 0

        var morningXp = 0
        var afternoonXp = 0
        var eveningXp = 0

        var workComp = 0
        var personalComp = 0
        var entertainmentComp = 0

        val checkCal = Calendar.getInstance()
        for (comp in completions) {
            checkCal.timeInMillis = comp.completedAt
            val hour = checkCal.get(Calendar.HOUR_OF_DAY)

            when {
                hour in 0..11 -> {
                    morningComp++
                    morningXp += comp.xpEarned
                }
                hour in 12..17 -> {
                    afternoonComp++
                    afternoonXp += comp.xpEarned
                }
                else -> {
                    eveningComp++
                    eveningXp += comp.xpEarned
                }
            }

            // Category breakdown
            when (comp.taskCategory.lowercase(Locale.ROOT)) {
                "work" -> workComp++
                "personal" -> personalComp++
                "entertainment" -> entertainmentComp++
            }
        }

        // We assume an ideal distribution of tasks across the day: totalTasksCount / 3 per period
        val countPerPeriod = if (totalTasksCount > 0) (totalTasksCount + 2) / 3 else 2

        val series = listOf(
            CalendarTimeSeriesItem("Morning", startOfDay, morningComp, countPerPeriod, morningXp, 
                CategoryBreakdown(work = completions.count { it.taskCategory == "Work" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 0..11 },
                                  personal = completions.count { it.taskCategory == "Personal" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 0..11 },
                                  entertainment = completions.count { it.taskCategory == "Entertainment" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 0..11 })),
            CalendarTimeSeriesItem("Afternoon", startOfDay + 12*60*60*1000, afternoonComp, countPerPeriod, afternoonXp,
                CategoryBreakdown(work = completions.count { it.taskCategory == "Work" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 12..17 },
                                  personal = completions.count { it.taskCategory == "Personal" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 12..17 },
                                  entertainment = completions.count { it.taskCategory == "Entertainment" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 12..17 })),
            CalendarTimeSeriesItem("Evening", startOfDay + 18*60*60*1000, eveningComp, countPerPeriod, eveningXp,
                CategoryBreakdown(work = completions.count { it.taskCategory == "Work" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 18..23 },
                                  personal = completions.count { it.taskCategory == "Personal" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 18..23 },
                                  entertainment = completions.count { it.taskCategory == "Entertainment" && Calendar.getInstance().apply { timeInMillis = it.completedAt }.get(Calendar.HOUR_OF_DAY) in 18..23 }))
        )

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = format.format(Date(targetDateMillis))

        return CalendarResponse(
            rangeType = "DAILY",
            startDate = dateStr,
            endDate = dateStr,
            totalTasks = totalTasksCount,
            completedTasks = completions.size,
            completionRate = if (totalTasksCount > 0) (completions.size.toFloat() / totalTasksCount * 100f) else 0f,
            totalXpEarned = completions.sumOf { it.xpEarned },
            timeSeriesData = series,
            categoryDistribution = CategoryBreakdown(work = workComp, personal = personalComp, entertainment = entertainmentComp)
        )
    }

    /**
     * Query 2: WEEKLY View Metrics
     * Groups task completions by day of week (Monday through Sunday).
     */
    suspend fun getWeeklyMetrics(targetDateMillis: Long, totalTasksCount: Int): CalendarResponse {
        val cal = Calendar.getInstance().apply { timeInMillis = targetDateMillis }
        
        // Find Monday of the current week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val (startOfWeek, _) = getDayRange(cal)
        
        // Find Sunday of current week
        cal.add(Calendar.DAY_OF_YEAR, 6)
        val (_, endOfWeek) = getDayRange(cal)

        // Retrieve completions
        val completions = taskDao.getCompletionsInRange(startOfWeek, endOfWeek)

        // Setup 7 days output
        val daysLabel = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val series = mutableListOf<CalendarTimeSeriesItem>()
        val checkCal = Calendar.getInstance()

        var workTotal = 0
        var personalTotal = 0
        var entertainmentTotal = 0

        for (i in 0..6) {
            // Find specific day millis
            val dayCal = Calendar.getInstance().apply {
                timeInMillis = startOfWeek
                add(Calendar.DAY_OF_YEAR, i)
            }
            val (dayStart, dayEnd) = getDayRange(dayCal)

            // Filter completions for this day
            val dayCompletions = completions.filter { it.completedAt in dayStart..dayEnd }
            val dayXp = dayCompletions.sumOf { it.xpEarned }

            val workCount = dayCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "work" }
            val personalCount = dayCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "personal" }
            val entertainmentCount = dayCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "entertainment" }

            workTotal += workCount
            personalTotal += personalCount
            entertainmentTotal += entertainmentCount

            series.add(
                CalendarTimeSeriesItem(
                    label = daysLabel[i],
                    timestamp = dayStart,
                    completedCount = dayCompletions.size,
                    totalCount = totalTasksCount, // Active task requirements per day
                    xpEarned = dayXp,
                    categoryBreakdown = CategoryBreakdown(workCount, personalCount, entertainmentCount)
                )
            )
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return CalendarResponse(
            rangeType = "WEEKLY",
            startDate = format.format(Date(startOfWeek)),
            endDate = format.format(Date(endOfWeek)),
            totalTasks = totalTasksCount * 7,
            completedTasks = completions.size,
            completionRate = if (totalTasksCount > 0) (completions.size.toFloat() / (totalTasksCount * 7) * 100f) else 0f,
            totalXpEarned = completions.sumOf { it.xpEarned },
            timeSeriesData = series,
            categoryDistribution = CategoryBreakdown(workTotal, personalTotal, entertainmentTotal)
        )
    }

    /**
     * Query 3: MONTHLY View Metrics
     * Groups task completions by 4 or 5 weeks of the month.
     */
    suspend fun getMonthlyMetrics(targetDateMillis: Long, totalTasksCount: Int): CalendarResponse {
        val cal = Calendar.getInstance().apply {
            timeInMillis = targetDateMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val (startOfMonth, _) = getDayRange(cal)

        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        cal.set(Calendar.DAY_OF_MONTH, lastDay)
        val (_, endOfMonth) = getDayRange(cal)

        val completions = taskDao.getCompletionsInRange(startOfMonth, endOfMonth)

        val series = mutableListOf<CalendarTimeSeriesItem>()
        var currentStart = startOfMonth
        var weekCount = 1

        var workTotal = 0
        var personalTotal = 0
        var entertainmentTotal = 0

        while (currentStart <= endOfMonth) {
            val weekEndCal = Calendar.getInstance().apply {
                timeInMillis = currentStart
                add(Calendar.DAY_OF_YEAR, 6)
            }
            val weekEnd = if (weekEndCal.timeInMillis > endOfMonth) endOfMonth else getDayRange(weekEndCal).second

            val weekCompletions = completions.filter { it.completedAt in currentStart..weekEnd }
            val weekXp = weekCompletions.sumOf { it.xpEarned }

            val workCount = weekCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "work" }
            val personalCount = weekCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "personal" }
            val entertainmentCount = weekCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "entertainment" }

            workTotal += workCount
            personalTotal += personalCount
            entertainmentTotal += entertainmentCount

            val daysInChunk = ((weekEnd - currentStart) / (24*60*60*1000) + 1).toInt()

            series.add(
                CalendarTimeSeriesItem(
                    label = "Week $weekCount",
                    timestamp = currentStart,
                    completedCount = weekCompletions.size,
                    totalCount = totalTasksCount * daysInChunk,
                    xpEarned = weekXp,
                    categoryBreakdown = CategoryBreakdown(workCount, personalCount, entertainmentCount)
                )
            )

            currentStart = weekEnd + 1
            weekCount++
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val daysInMonth = ((endOfMonth - startOfMonth) / (24*60*60*1000) + 1).toInt()

        return CalendarResponse(
            rangeType = "MONTHLY",
            startDate = format.format(Date(startOfMonth)),
            endDate = format.format(Date(endOfMonth)),
            totalTasks = totalTasksCount * daysInMonth,
            completedTasks = completions.size,
            completionRate = if (totalTasksCount > 0) (completions.size.toFloat() / (totalTasksCount * daysInMonth) * 100f) else 0f,
            totalXpEarned = completions.sumOf { it.xpEarned },
            timeSeriesData = series,
            categoryDistribution = CategoryBreakdown(workTotal, personalTotal, entertainmentTotal)
        )
    }

    /**
     * Query 4: YEARLY View Metrics
     * Groups task completions by the 12 calendar months.
     */
    suspend fun getYearlyMetrics(targetDateMillis: Long, totalTasksCount: Int): CalendarResponse {
        val cal = Calendar.getInstance().apply {
            timeInMillis = targetDateMillis
            set(Calendar.MONTH, Calendar.JANUARY)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val (startOfYear, _) = getDayRange(cal)

        cal.set(Calendar.MONTH, Calendar.DECEMBER)
        cal.set(Calendar.DAY_OF_MONTH, 31)
        val (_, endOfYear) = getDayRange(cal)

        val completions = taskDao.getCompletionsInRange(startOfYear, endOfYear)
        val monthsLabel = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val series = mutableListOf<CalendarTimeSeriesItem>()

        var workTotal = 0
        var personalTotal = 0
        var entertainmentTotal = 0

        for (m in 0..11) {
            val monthStartCal = Calendar.getInstance().apply {
                timeInMillis = startOfYear
                set(Calendar.MONTH, m)
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val (mStart, _) = getDayRange(monthStartCal)

            val mEndCal = Calendar.getInstance().apply {
                timeInMillis = mStart
                set(Calendar.DAY_OF_MONTH, monthStartCal.getActualMaximum(Calendar.DAY_OF_MONTH))
            }
            val (_, mEnd) = getDayRange(mEndCal)

            val monthCompletions = completions.filter { it.completedAt in mStart..mEnd }
            val mXp = monthCompletions.sumOf { it.xpEarned }

            val workCount = monthCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "work" }
            val personalCount = monthCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "personal" }
            val entertainmentCount = monthCompletions.count { it.taskCategory.lowercase(Locale.ROOT) == "entertainment" }

            workTotal += workCount
            personalTotal += personalCount
            entertainmentTotal += entertainmentCount

            val daysInMonth = monthStartCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            series.add(
                CalendarTimeSeriesItem(
                    label = monthsLabel[m],
                    timestamp = mStart,
                    completedCount = monthCompletions.size,
                    totalCount = totalTasksCount * daysInMonth,
                    xpEarned = mXp,
                    categoryBreakdown = CategoryBreakdown(workCount, personalCount, entertainmentCount)
                )
            )
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val daysInYear = if (GregorianCalendar().isLeapYear(cal.get(Calendar.YEAR))) 366 else 365

        return CalendarResponse(
            rangeType = "YEARLY",
            startDate = format.format(Date(startOfYear)),
            endDate = format.format(Date(endOfYear)),
            totalTasks = totalTasksCount * daysInYear,
            completedTasks = completions.size,
            completionRate = if (totalTasksCount > 0) (completions.size.toFloat() / (totalTasksCount * daysInYear) * 100f) else 0f,
            totalXpEarned = completions.sumOf { it.xpEarned },
            timeSeriesData = series,
            categoryDistribution = CategoryBreakdown(workTotal, personalTotal, entertainmentTotal)
        )
    }


    // --- Helper Date-Time functions ---

    private fun getDayRange(cal: Calendar): Pair<Long, Long> {
        val checkCal = cal.clone() as Calendar
        checkCal.set(Calendar.HOUR_OF_DAY, 4)
        checkCal.set(Calendar.MINUTE, 0)
        checkCal.set(Calendar.SECOND, 0)
        checkCal.set(Calendar.MILLISECOND, 0)
        val start = checkCal.timeInMillis

        val endCal = cal.clone() as Calendar
        endCal.add(Calendar.DAY_OF_YEAR, 1)
        endCal.set(Calendar.HOUR_OF_DAY, 3)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        endCal.set(Calendar.MILLISECOND, 999)
        val end = endCal.timeInMillis

        return Pair(start, end)
    }

    private fun isConsecutiveDay(prev: Calendar, curr: Calendar): Boolean {
        val temp = prev.clone() as Calendar
        temp.add(Calendar.DAY_OF_YEAR, 1)
        return temp.get(Calendar.YEAR) == curr.get(Calendar.YEAR) &&
               temp.get(Calendar.DAY_OF_YEAR) == curr.get(Calendar.DAY_OF_YEAR)
    }
}
