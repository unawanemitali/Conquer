package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // --- Task CRUD ---
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasksFlow(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId")
    suspend fun deleteCompletionsByTaskId(taskId: Int)

    @Query("UPDATE tasks SET isCompleted = :isCompleted, streak = :streak, lastCompletedAt = :lastCompletedAt, lastCompletedDate = :lastCompletedDate, completedOnLogicalDate = :completedOnLogicalDate, history = :history WHERE id = :id")
    suspend fun updateTaskCompletionState(id: Int, isCompleted: Boolean, streak: Int, lastCompletedAt: Long, lastCompletedDate: String, completedOnLogicalDate: String, history: List<String>)


    // --- TaskCompletion Time-Series Queries ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: TaskCompletion): Long

    @Query("DELETE FROM task_completions WHERE taskId = :taskId AND completedAt >= :startOfDay AND completedAt <= :endOfDay")
    suspend fun deleteCompletionForDay(taskId: Int, startOfDay: Long, endOfDay: Long)

    @Query("SELECT * FROM task_completions ORDER BY completedAt DESC")
    fun getAllCompletionsFlow(): Flow<List<TaskCompletion>>

    @Query("SELECT * FROM task_completions WHERE completedAt BETWEEN :startTime AND :endTime ORDER BY completedAt ASC")
    suspend fun getCompletionsInRange(startTime: Long, endTime: Long): List<TaskCompletion>

    @Query("SELECT SUM(xpEarned) FROM task_completions")
    fun getTotalXpFlow(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM task_completions WHERE taskCategory = :category")
    suspend fun getCompletionCountByCategory(category: String): Int

    // --- FocusSession Queries ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession): Long

    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
    fun getAllFocusSessionsFlow(): Flow<List<FocusSession>>

    @Query("SELECT * FROM focus_sessions WHERE completedAt BETWEEN :startTime AND :endTime ORDER BY completedAt ASC")
    suspend fun getFocusSessionsInRange(startTime: Long, endTime: Long): List<FocusSession>

    // --- DailyNotes Queries ---
    @Query("SELECT * FROM daily_notes WHERE dateStr = :dateStr")
    suspend fun getDailyNoteByDate(dateStr: String): DailyNote?

    @Query("SELECT * FROM daily_notes ORDER BY lastUpdated DESC")
    fun getAllDailyNotesFlow(): Flow<List<DailyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyNote(note: DailyNote)
}
