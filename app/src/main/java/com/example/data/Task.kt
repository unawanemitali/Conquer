package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Work", "Personal", "Entertainment"
    val xp: Int = 10,
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val isRecurring: Boolean = false, // true = Habit/Daily, false = One-time task
    val difficulty: String = "Medium", // "Easy", "Medium", "Hard", "Mythic"
    val createdAt: Long = System.currentTimeMillis(),
    val lastCompletedAt: Long = 0L,
    val assignedDateMillis: Long = System.currentTimeMillis(), // New future assignment date
    val frequencyDaysOfWeek: Int = 7, // Habit frequency (1-7 days a week)
    val priorityQuadrant: String = "Not Urgent & Unimportant",
    val usedMidnightOil: Boolean = false,
    val lastCompletedDate: String = "",
    val completedOnLogicalDate: String = "",
    val history: List<String> = emptyList()
)

@Entity(tableName = "daily_notes")
data class DailyNote(
    @PrimaryKey val dateStr: String, // key format e.g. "2026-06-01"
    val note: String,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_completions")
data class TaskCompletion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val taskTitle: String,
    val taskCategory: String, // To easily group history even if task changes
    val completedAt: Long = System.currentTimeMillis(),
    val xpEarned: Int = 10,
    val completedOnLogicalDate: String = ""
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val taskTitle: String,
    val durationSeconds: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val status: String, // "Completed" or "Interrupted"
    val xpEarned: Int = 0
)

@Entity(tableName = "activities_catalog")
data class CatalogItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // "Work", "Personal", "Entertainment"
    val tier: Int // 1, 2, or 3
)

