package com.slowmotion.alarmapp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.*
import java.util.concurrent.locks.Lock


@Database(
    entities = [alarm::class],
    version = 2

)

abstract class alarmDB : RoomDatabase() {
    abstract fun alarmDao() : alarmDao

    companion object{
        @Volatile
        private var instance: alarmDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also{
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, alarmDB::class.java, "alarm12345.db")
                .fallbackToDestructiveMigration()
                .build()

    }
}