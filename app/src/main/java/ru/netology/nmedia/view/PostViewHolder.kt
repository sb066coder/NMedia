package ru.netology.nmedia.view

import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.model.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnShareListener
) : RecyclerView.ViewHolder(binding.root){

    fun bind(post: Post) {
        binding.apply {
            // Заполнение лэйаута данными из поста
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = post.content
            ivLike.setImageResource(if (post.likedByMe) R.drawable.ic_heart_red else R.drawable.ic_heart_grey)
            ivLike.setOnClickListener { onLikeListener(post) }
            ivShare.setOnClickListener { onShareListener(post) }
            tvLikes.text = formattedNumber(post.likes)
            tvShared.text = formattedNumber(post.shares)
            tvWatched.text = formattedNumber(post.watches)
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
