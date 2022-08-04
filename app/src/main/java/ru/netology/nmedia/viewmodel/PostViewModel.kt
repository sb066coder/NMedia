package ru.netology.nmedia.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.model.PostRepository
import ru.netology.nmedia.model.PostRepositoryInMemoryImpl

class PostViewModel: ViewModel() {
    //Создание репозитория
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    // Поле data получает ссылку на data репозитория. За следит activity
    val data = repository.getAll()
    // Перенапрвление вызова функции лайк из activity в репозиторий
    fun likeById(id: Long) {
        Log.i("AAA", "fun likeById in viewModel called")
        repository.likeById(id)
    }
    fun shareById(id: Long) = repository.shareById(id)
}