package com.example.eventplanerapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eventplanerapp.model.Event

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM events WHERE dateTime BETWEEN :start AND :end ORDER BY dateTime ASC")
    fun getEventsBetween(start: Long, end: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE dateTime >= :now ORDER BY dateTime ASC")
    fun getUpcomingEvents(now: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Long): Event?

    @Query("""
    SELECT MIN(dateTime) as dateTime
    FROM events
    GROUP BY strftime('%Y-%m-%d', dateTime / 1000, 'unixepoch')
    ORDER BY dateTime ASC
""")
    fun getAllEventDatesLive(): LiveData<List<Long>>

}