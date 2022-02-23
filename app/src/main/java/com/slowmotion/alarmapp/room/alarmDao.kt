package com.slowmotion.alarmapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface alarmDao {
    @Insert
    fun AddAlarm (alarm: alarm)

    @Query("SELECT * FROM alarm")

    fun getAlarm(): LiveData<List<alarm>>

    @Delete
    fun deleteAlarm(alarm: alarm)
}