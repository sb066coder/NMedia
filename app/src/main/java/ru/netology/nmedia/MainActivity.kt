package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = createSamplePost()
        applyPost(binding, post)

        ivLikes.setOnClickListener {
            println("heart clicked")
            post.likes += if (!post.likedByMe) 1 else -1
            post.likedByMe = !post.likedByMe
            applyPost(binding, post)
        }

        ivShare.setOnClickListener {
            println("share clicked")
            post.shares++
            applyPost(binding, post)
        }

        binding.root.setOnClickListener {
            println("root clicked")
        }

        ibMenu.setOnClickListener {
            println("menu clicked")
        }

        avatar.setOnClickListener {
            println("avatar clicked")
        }
    }

    private fun applyPost(binding: ActivityMainBinding, post: Post) {
        with(binding) {
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = post.content
            ivLikes.setImageResource(if (post.likedByMe) R.drawable.ic_heart_red else R.drawable.ic_heart_grey)
            tvLikes.text = formattedCount(post.likes)
            tvShared.text = formattedCount(post.shares)
            tvWatched.text = formattedCount(post.watches)
        }
    }

    private fun formattedCount(likes: Int): String {
        return when {
            likes < 1_000 -> likes.toString()
            likes < 10_000 -> "${likes / 1000}" + (if ((likes / 100) % 10 > 0) ".${(likes / 100) % 10}" else "") + " K"
            likes < 1_000_000 -> "${likes / 1000} K"
            likes < 10_000_000 -> "${likes / 1_000_000}" + (if ((likes / 100_000) % 10 > 0) ".${(likes / 100_000) % 10}" else "") + " M"
            else -> "${likes / 1_000_000} M"
        }
    }

    private fun createSamplePost(): Post {
        return Post(
            1,
            "Нетология. Университет интернет-профессий будущего",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            "21 мая в 18:36",
            995,
            1895,
            15_520_000,
            false
        )
    }
}