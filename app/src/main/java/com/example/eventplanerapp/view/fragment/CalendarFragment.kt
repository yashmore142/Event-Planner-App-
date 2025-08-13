package com.example.eventplanerapp.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventplanerapp.constant.Constant.startOfDayMillis
import com.example.eventplanerapp.constant.EventDecorator
import com.example.eventplanerapp.databinding.FragmentCalendarBinding
import com.example.eventplanerapp.view.activity.MainActivity
import com.example.eventplanerapp.view.adapter.EventAdapter
import com.example.eventplanerapp.viewmodel.EventViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private var viewModel: EventViewModel?= null
    private lateinit var adapter: EventAdapter
    private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        adapter = EventAdapter(onEdit = { ev -> openAddEdit(ev.id) }, onDelete = { ev ->
            viewModel!!.delete(ev)
        })

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter
        val today = System.currentTimeMillis()
        binding.tvSelectedDate.text = dateFormat.format(Date(today))
        viewModel!!.selectDay(startOfDayMillis(today))
        binding.calendarView.selectedDate = CalendarDay.today()

        observer()
        binding.calendarView.setOnDateChangedListener( object : OnDateSelectedListener {
            override fun onDateSelected(
                widget: MaterialCalendarView,
                date: CalendarDay,
                selected: Boolean
            ) {
                if (selected) {
                    val cal = Calendar.getInstance()
                    cal.set(date.year, date.month , date.day, 0, 0, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    val start = cal.timeInMillis
                    binding.tvSelectedDate.text = dateFormat.format(Date(start))
                    viewModel?.selectDay(start)
                }
            }
        })
        binding.fabAdd.setOnClickListener {
            val selectedStart = viewModel!!.selectedDayStart.value ?: startOfDayMillis(System.currentTimeMillis())
            openAddEdit(null, selectedStart)
        }
    }

    private fun observer() {
        viewModel!!.eventsForSelectedDay.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.tvNoEvents.visibility = View.VISIBLE
                binding.rvEvents.visibility = View.GONE
            } else {
                binding.tvNoEvents.visibility = View.GONE
                binding.rvEvents.visibility = View.VISIBLE
            }

            adapter.submitList(list)
        }
        viewModel!!.getAllEventDates.observe(viewLifecycleOwner) { list ->
            val calendarDays = list.map { time ->
                val cal = Calendar.getInstance().apply { timeInMillis  = time }
                CalendarDay.from(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
            }
            binding.calendarView.addDecorator(EventDecorator(Color.RED, calendarDays))
        }
    }

    private fun openAddEdit(eventId: Long? = null, dayStart: Long? = null) {
        val frag = AddEditEventFragment.newInstance(eventId ?: -1L,
            dayStart ?: -1L
        )
        requireActivity().supportFragmentManager.beginTransaction()
            .replace((requireActivity() as MainActivity).binding.fragmentContainer.id, frag)
            .addToBackStack(null)
            .commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}