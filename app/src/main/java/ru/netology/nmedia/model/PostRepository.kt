package ru.netology.nmedia.model

import androidx.lifecycle.LiveData

interface PostRepository {
    fun get(): LiveData<Post>
    fun like()
    fun share()
}