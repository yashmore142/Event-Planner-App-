package com.example.eventplanerapp.view.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.eventplanerapp.R
import com.example.eventplanerapp.databinding.FragmentAddEditEventBinding
import com.example.eventplanerapp.model.Event
import com.example.eventplanerapp.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddEditEventFragment : Fragment() {

    private var _binding: FragmentAddEditEventBinding? = null
    private val binding get() = _binding!!
    private var viewModel: EventViewModel? = null
    private var editingEventId: Long? = null
    private var pickedDateTimeMillis: Long = System.currentTimeMillis()
    private val fullFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val id = it.getLong(ARG_EVENT_ID, -1L)
            if (id != -1L) editingEventId = id
            val preDayStart = it.getLong(ARG_DAY_START, -1L)
            if (preDayStart != -1L) {
                pickedDateTimeMillis =
                    preDayStart + 60 * 60 * 1000
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        _binding = FragmentAddEditEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateDateTimeUI()

        if (editingEventId != null) {
            lifecycleScope.launch {
                val ev = viewModel!!.getEventById(editingEventId!!)
                ev?.let {
                    binding.etTitle.setText(it.title)
                    binding.etDescription.setText(it.description)
                    pickedDateTimeMillis = it.dateTime
                    updateDateTimeUI()
                }
            }
        }

        binding.tvDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = pickedDateTimeMillis }
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    val c = Calendar.getInstance()
                    c.set(
                        y,
                        m,
                        d,
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE)
                    )
                    val oldTime =
                        Calendar.getInstance().apply { timeInMillis = pickedDateTimeMillis }
                    c.set(Calendar.HOUR_OF_DAY, oldTime.get(Calendar.HOUR_OF_DAY))
                    c.set(Calendar.MINUTE, oldTime.get(Calendar.MINUTE))
                    pickedDateTimeMillis = c.timeInMillis
                    updateDateTimeUI()
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()

            datePickerDialog.show()

        }

        binding.tvTime.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = pickedDateTimeMillis }
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val c = Calendar.getInstance().apply { timeInMillis = pickedDateTimeMillis }
                c.set(Calendar.HOUR_OF_DAY, hour)
                c.set(Calendar.MINUTE, minute)
                pickedDateTimeMillis = c.timeInMillis
                updateDateTimeUI()
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            if (title.isEmpty()) {
                binding.etTitle.error = "Enter title"
                return@setOnClickListener
            }
            val desc = binding.etDescription.text.toString().trim()
            val event = if (editingEventId != null) {
                Event(editingEventId!!, title, desc, pickedDateTimeMillis)
            } else {
                Event(title = title, description = desc, dateTime = pickedDateTimeMillis)
            }

            if (editingEventId != null) {
                viewModel?.update(event) { requireActivity().supportFragmentManager.popBackStack() }
            } else {
                viewModel?.insert(event) {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun updateDateTimeUI() {
        binding.tvDate.setText(
            fullFormat.format(Date(pickedDateTimeMillis)).split(",")[0]
        )
        binding.tvTime.setText(fullFormat.format(Date(pickedDateTimeMillis)).split(",")[1].trim())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_EVENT_ID = "arg_event_id"
        private const val ARG_DAY_START = "arg_day_start"

        fun newInstance(eventId: Long = -1L, dayStart: Long = -1L): AddEditEventFragment {
            val f = AddEditEventFragment()
            f.arguments = Bundle().apply {
                putLong(ARG_EVENT_ID, eventId)
                putLong(ARG_DAY_START, dayStart)
            }
            return f
        }
    }
}