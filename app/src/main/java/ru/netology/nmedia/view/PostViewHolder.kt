package ru.netology.nmedia.view

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.api.BASE_URL
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.ViewUtils

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

            val url = "$G_BASE_URL/avatars/${post.authorAvatar}"
            Glide.with(ivAvatar)
                .load(url)
                .timeout(10_000)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_cancel_24)
                .circleCrop()
                .into(ivAvatar)

            if (post.attachment != null) {
                ivContent.visibility = View.VISIBLE
                Glide.with(ivContent)
                    .load("$G_BASE_URL/images/${post.attachment.url}")
                    .timeout(10_000)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.ic_baseline_cancel_24)
                    .into(ivContent)
            } else {
                ivContent.visibility = View.GONE
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
