package ru.netology.nmedia.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityMainBinding
import androidx.activity.viewModels
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { post ->
            with(binding) {
                tvAuthor.text = post.author
                tvPublished.text = post.published
                tvContent.text = post.content
                ivLikes.setImageResource(if (post.likedByMe) R.drawable.ic_heart_red else R.drawable.ic_heart_grey)
                tvLikes.text = formattedNumber(post.likes)
                tvShared.text = formattedNumber(post.shares)
                tvWatched.text = formattedNumber(post.watches)
            }
        }

        binding.ivLikes.setOnClickListener {
            viewModel.like()
        }

        binding.ivShare.setOnClickListener {
            viewModel.share()
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