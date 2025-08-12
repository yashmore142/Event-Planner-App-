package com.example.eventplanerapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventplanerapp.R
import com.example.eventplanerapp.databinding.FragmentUpcomingBinding
import com.example.eventplanerapp.view.activity.MainActivity
import com.example.eventplanerapp.view.adapter.EventAdapter
import com.example.eventplanerapp.viewmodel.EventViewModel


class UpcomingFragment : Fragment(){

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private var viewModel: EventViewModel? = null
    private lateinit var adapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(EventViewModel::class.java)
        adapter = EventAdapter(onEdit = { ev -> openAddEdit(ev.id) }, onDelete = { ev -> viewModel!!.delete(ev) })
        binding.rvUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcoming.adapter = adapter

        viewModel?.upcomingEvents?.observe(viewLifecycleOwner) { list ->
            if (!list.isEmpty()) {
                adapter.submitList(list)
                binding.tvNoUpcoming.visibility = View.GONE
                binding.rvUpcoming.visibility = View.VISIBLE
            }else{
                binding.tvNoUpcoming.visibility = View.VISIBLE
                binding.rvUpcoming.visibility = View.GONE
            }
        }
    }

    private fun openAddEdit(eventId: Long) {
        val frag = AddEditEventFragment.newInstance(eventId)
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