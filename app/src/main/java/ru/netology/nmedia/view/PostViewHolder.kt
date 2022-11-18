package ru.netology.nmedia.view

import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.ViewUtils

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
            mbLike.text = ViewUtils.formattedNumber(post.likes)
            mbShare.text = ViewUtils.formattedNumber(post.shares)
            mbWatch.text = ViewUtils.formattedNumber(post.watches)

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
            root.setOnClickListener { onInteractionListener.onOpenPost(post) }
        }
    }

    fun bindOnLikeChanged(post: Post) {
        with(binding.mbLike) {
            isChecked = post.likedByMe
            text = ViewUtils.formattedNumber(post.likes)
            setOnClickListener { onInteractionListener.onLike(post) }
        }

    }
}
