package com.example.eventplanerapp.database

import androidx.lifecycle.LiveData
import com.example.eventplanerapp.model.Event

class EventRepository(private val dao: EventDao) {

    suspend fun insert(event: Event) = dao.insert(event)
    suspend fun update(event: Event) = dao.update(event)
    suspend fun delete(event: Event) = dao.delete(event)
    suspend fun getEventById(id: Long) = dao.getEventById(id)
    fun getAllEventDatesLive() = dao.getAllEventDatesLive()

    fun getEventsBetween(start: Long, end: Long): LiveData<List<Event>> =
        dao.getEventsBetween(start, end)

    fun getUpcomingEvents(now: Long): LiveData<List<Event>> =
        dao.getUpcomingEvents(now)
}