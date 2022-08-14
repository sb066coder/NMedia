package ru.netology.nmedia.view

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.model.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root){

    fun bind(post: Post) {
        binding.apply {
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = post.content
            mbLike.isChecked = post.likedByMe
            mbLike.setOnClickListener { onInteractionListener.onLike(post) }
            mbShare.setOnClickListener { onInteractionListener.onShare(post) }
            mbLike.text = formattedNumber(post.likes)
            mbShare.text = formattedNumber(post.shares)
            mbWatch.text = formattedNumber(post.watches)

            mbMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
            ivVideoContent.visibility = if (post.videoContent != null)  View.VISIBLE else View.GONE
            ivVideoContent.setOnClickListener { onInteractionListener.onViewVideo(post) }
        }
    }

    private fun formattedNumber(number: Int): String {
        return when {
            number < 1_000 -> number.toString()
            number < 10_000 -> "${number / 1000}" + (if ((number / 100) % 10 > 0) ".${(number / 100) % 10}" else "") + " K"
            number < 1_000_000 -> "${number / 1000} K"
            number < 10_000_000 -> "${number / 1_000_000}" + (if ((number / 100_000) % 10 > 0) ".${(number / 100_000) % 10}" else "") + " M"
            else -> "${number / 1_000_000} M"
        }
    }
}
