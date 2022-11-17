package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val empty = Post(
    id = 0,
    author = "",
    content = "",
    published = "",
    likes = 0,
    shares = 0,
    watches = 0,
    likedByMe = false
)

class PostViewModel(application: Application): AndroidViewModel(application) {


    //Создание репозитория
    private val repository: PostRepository = PostRepositoryServerImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    init {
        loadPosts()
    }

    fun loadPosts(onRefresh: Boolean = false) {
        // Start loading
        _data.postValue(FeedModel(loading = !onRefresh))

        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                // if data got successfully
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
                _isRefreshing.postValue(false)
            }
            override fun onError(e: Exception) {
                // if error got
                _data.postValue(FeedModel(error = true))
                _isRefreshing.postValue(false)
            }
        })
    }

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        thread {
            val updatedPost = if (_data.value?.posts?.first { it.id == id }?.likedByMe == false) {
                repository.likeById(id)
                } else {
                repository.unLikeById(id)
            }
            _data.postValue(_data.value?.copy(posts = _data.value?.posts.orEmpty().let {
                val updatedList = mutableListOf<Post>()
                for (post in it) {
                    if (post.id == updatedPost.id) {
                        updatedList.add(updatedPost)
                    } else {
                        updatedList.add(post)
                    }
                }
                updatedList
            } ))
        }
    }

//    fun shareById(id: Long) = repository.shareById(id)
    fun deleteById(id: Long) {
        thread {
            // Оптимистическая модель
            val old = _data.value?.posts.orEmpty()
            val new = _data.value?.posts.orEmpty().filter { it.id != id }
            _data.postValue(_data.value?.copy(posts = new, empty = new.isEmpty()))
            try {
                repository.deleteById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun refreshPosts() {
        _isRefreshing.value = true
        loadPosts(onRefresh = true)
    }
}