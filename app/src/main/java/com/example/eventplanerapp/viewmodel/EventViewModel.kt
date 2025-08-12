package com.example.eventplanerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventplanerapp.database.AppDatabase
import com.example.eventplanerapp.database.EventRepository
import com.example.eventplanerapp.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: EventRepository

    // Selected day epoch millis (start of day)
    private val _selectedDayStart = MutableLiveData<Long>()
    val selectedDayStart: LiveData<Long> get() = _selectedDayStart

    // Expose events for selected day using switchMap
    val eventsForSelectedDay = MutableLiveData<List<Event>>()

    // Upcoming events
    val upcomingEvents: LiveData<List<Event>>

    init {
        val db = AppDatabase.getDatabase(application)
        repo = EventRepository(db.eventDao())

        // default selected day = today start
        _selectedDayStart.value = startOfDayMillis(System.currentTimeMillis())

        _selectedDayStart.observeForever { start ->
            val end = endOfDayMillis(start)
            repo.getEventsBetween(start, end).observeForever { events ->
                eventsForSelectedDay.value = events
            }
        }

        upcomingEvents = repo.getUpcomingEvents(System.currentTimeMillis())
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

    companion object {
        // helpers for day start/end
        fun startOfDayMillis(timeMillis: Long): Long {
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = timeMillis }
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        fun endOfDayMillis(startOfDayMillis: Long): Long {
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = startOfDayMillis }
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
            cal.add(java.util.Calendar.MILLISECOND, -1)
            return cal.timeInMillis
        }
    }
}