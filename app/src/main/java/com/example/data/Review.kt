package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reviewType: String, // "Weekly" or "Monthly"
    val createdAt: Long = System.currentTimeMillis(),
    val wentWell: String,
    val distractions: String
)
