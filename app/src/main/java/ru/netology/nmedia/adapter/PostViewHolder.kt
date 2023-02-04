package ru.netology.nmedia.adapter

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.ViewUtils
import ru.netology.nmedia.view.OnInteractionListener
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root){

    companion object {
        const val G_BASE_URL = "http://10.0.2.2:9999"   // base URL for using in Glide
    }

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

            mbMenu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE

            mbMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    // TODO: if we don't have other options, just remove dots
                    menu.setGroupVisible(R.id.owner, post.ownedByMe)
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

            val url = "$G_BASE_URL/avatars/${post.authorAvatar}"
            ivAvatar.loadCircleCrop(url)

            if (post.attachment != null) {
                ivContent.visibility = View.VISIBLE
                ivContent.load("$G_BASE_URL/media/${post.attachment.url}")
            } else {
                ivContent.visibility = View.GONE
            }

            ivContent.setOnClickListener {
                onInteractionListener.onOpenImage("$G_BASE_URL/media/${post.attachment?.url}")
            }

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
