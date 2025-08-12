package com.example.eventplanerapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eventplanerapp.model.Event

@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class AppDatabase  : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_planner_db"
                ).build()
                instance = inst
                inst
            }
    }
}