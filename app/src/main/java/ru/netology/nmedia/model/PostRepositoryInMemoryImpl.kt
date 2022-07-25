package ru.netology.nmedia.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostRepositoryInMemoryImpl: PostRepository {
    private var post = Post(
        1,
        "Нетология. Университет интернет-профессий будущего",
        "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        "21 мая в 18:36",
        995,
        1895,
        15_520_000,
        false
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> {
        return data
    }

    override fun like() {
        post = post.copy(
            likedByMe = !post.likedByMe,
            likes = if (!post.likedByMe) post.likes + 1 else post.likes - 1
        )
        data.value = post
    }

    override fun share() {
        post = post.copy(shares = post.shares + 1)
        data.value = post
    }
}