package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardTimingSeparatorBinding
import ru.netology.nmedia.model.Ad
import ru.netology.nmedia.model.FeedItem
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.TimingSeparator
import ru.netology.nmedia.view.OnInteractionListener

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
    ) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is TimingSeparator -> R.layout.card_timing_separator
            null -> error("unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            R.layout.card_timing_separator -> {
                val binding = CardTimingSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TimingSeparatorViewHolder(binding)
            }
            else -> error("unknown view type: $viewType")
        }


    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is TimingSeparator -> (holder as? TimingSeparatorViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                (it as? Payload)?.let { payloads ->
                    (holder as PostViewHolder).bind(payloads)
                }
            }
        }
    }
}

data class Payload(
    val likedByMe: Boolean? = null,
    val likes: Int? = null,
    val content: String? = null,

)






















