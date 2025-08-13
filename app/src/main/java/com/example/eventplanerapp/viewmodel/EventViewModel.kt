package com.example.eventplanerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventplanerapp.constant.Constant.endOfDayMillis
import com.example.eventplanerapp.constant.Constant.startOfDayMillis
import com.example.eventplanerapp.database.AppDatabase
import com.example.eventplanerapp.database.EventRepository
import com.example.eventplanerapp.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: EventRepository
    private val _selectedDayStart = MutableLiveData<Long>()
    val selectedDayStart: LiveData<Long> get() = _selectedDayStart
    val eventsForSelectedDay = MutableLiveData<List<Event>>()
    val upcomingEvents: LiveData<List<Event>>
    val getAllEventDates: LiveData<List<Long>>

    init {
        val db = AppDatabase.getDatabase(application)
        repo = EventRepository(db.eventDao())

        _selectedDayStart.value = startOfDayMillis(System.currentTimeMillis())

        _selectedDayStart.observeForever { start ->
            val end = endOfDayMillis(start)
            repo.getEventsBetween(start, end).observeForever { events ->
                eventsForSelectedDay.value = events
            }
        }

        upcomingEvents = repo.getUpcomingEvents(System.currentTimeMillis())
        getAllEventDates = repo.getAllEventDatesLive()
    }

    fun selectDay(startOfDayMillis: Long) {
        _selectedDayStart.value = startOfDayMillis
    }

    fun insert(event: Event, onDone: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insert(event)
            onDone?.invoke()
        }
    }

    fun update(event: Event, onDone: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.update(event)
            onDone?.invoke()
        }
    }

    fun delete(event: Event, onDone: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.delete(event)
            onDone?.invoke()
        }
    }

    suspend fun getEventById(id: Long): Event? {
        return repo.getEventById(id)
    }
}