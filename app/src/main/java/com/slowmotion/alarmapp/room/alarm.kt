package com.slowmotion.alarmapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class alarm (
    @PrimaryKey (autoGenerate = true)
    val id: Int,
    val time: String,
    val date: String,
    val note: String,
    val type: Int
)