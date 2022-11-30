package com.example.sectionedrecyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sectionedrecyclerview.databinding.DateItemBinding
import com.example.sectionedrecyclerview.databinding.GeneralItemBinding

class Adapter (
    private val items: List<ListItem>
        ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ListItem.TYPE_DATE ->
                DateViewHolder(DateItemBinding.inflate(layoutInflater))
            else ->
                GeneralViewHolder(GeneralItemBinding.inflate(layoutInflater))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ListItem.TYPE_DATE -> (holder as DateViewHolder).bind(
                item = items[position] as DateItem,
            )
            ListItem.TYPE_GENERAL -> (holder as GeneralViewHolder).bind(
                item = items[position] as GeneralItem
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class DateViewHolder(val binding: DateItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DateItem) {
            // convert to format 'DD MM YYYY' 
            val rawDate = item.date
            val year = rawDate.take(4)
            val monthDay = rawDate.takeLast(4)
            val month = monthDay.take(2)
            val day = monthDay.takeLast(2)

            val actualDate = "$day $month $year"
            binding.txtDate.text = actualDate
        }
    }

    inner class GeneralViewHolder(val binding: GeneralItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GeneralItem) {
            binding.txtTitle.text = item.name
        }
    }
}