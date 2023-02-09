package ru.netology.nmedia.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardTimingSeparatorBinding
import ru.netology.nmedia.model.TimingSeparator

class TimingSeparatorViewHolder(
    private val binding: CardTimingSeparatorBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(timingSeparator: TimingSeparator) {
        binding.tvSeparator.setText(
            when(timingSeparator.title) {
                TimingSeparator.Period.TODAY -> R.string.today
                TimingSeparator.Period.YESTERDAY -> R.string.yesterday
                TimingSeparator.Period.LAST_WEEK -> R.string.lastWeek
            }
        )
    }
}
