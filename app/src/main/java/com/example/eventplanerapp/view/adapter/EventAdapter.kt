package com.example.eventplanerapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventplanerapp.databinding.ItemEventBinding
import com.example.eventplanerapp.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(
    private val onEdit: (Event) -> Unit,
    private val onDelete: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private val items = mutableListOf<Event>()
    private val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateShortFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    fun submitList(list: List<Event>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ev: Event) {
            binding.tvTitle.text = ev.title
            binding.tvDesc.text = ev.description ?: ""
            binding.tvTime.text = dateShortFormat.format(Date(ev.dateTime))
            binding.btnEdit.setOnClickListener { onEdit(ev) }
            binding.btnDelete.setOnClickListener { onDelete(ev) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}