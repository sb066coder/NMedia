package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "",
    likes = 0,
    shares = 0,
    watches = 0,
    likedByMe = false
)

class PostViewModel(application: Application): AndroidViewModel(application) {

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

        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(response: List<Post>) {
                // if data got successfully
                _data.postValue(FeedModel(posts = response, empty = response.isEmpty()))
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
        _data.postValue(_data.value?.copy(loading = true))  // включаем прогресс загрузки
        edited.value?.let { edited ->   // берем измененный пост
            repository.saveAsync(edited, object : PostRepository.Callback<Post> {   // выполняем асинхронный вызов функции saveAsync
                override fun onSuccess(response: Post) {    // в случае успеха
                    _data.postValue(FeedModel(_data.value?.posts.orEmpty().let { list ->
                        if (edited.id == 0L) {  // если пост новый
                            list.toMutableList().also { it.add(0, response) }   // добавляем его в список
                        } else {    // если пост старый и мы его редактируем
                            val newList = mutableListOf<Post>() // заменяем старый пост новым
                            list.forEach { if (it.id == response.id) {
                                    newList.add(response)
                                } else {
                                    newList.add(it)
                                }}
                            newList
                        }
                    }))
                    _postCreated.postValue(Unit)    // записываем в _postCreated, что создали пост
                }
                override fun onError(e: Exception) {    // в случае неуспеха
                    Toast.makeText(getApplication(), "Server error appeared", Toast.LENGTH_LONG).show() // сообщаем об ошибке
                    _data.postValue(_data.value?.copy(loading = false)) // выключаем прогресс загрузки
                }
            })
        }
        edited.value = empty    // записываем в edited новый пустой пост
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
        _data.postValue(_data.value?.copy(loading = true))
        val plusLike = !(_data.value?.posts?.first { it.id == id }?.likedByMe ?: false)
        repository.likeByIdAsync(plusLike, id, object : PostRepository.Callback<Post> {
            override fun onSuccess(response: Post) {
                _data.postValue(FeedModel(posts = _data.value?.posts.orEmpty().let {
                    val updatedList = mutableListOf<Post>()
                    for (post in it) {
                        if (post.id == response.id) {
                            updatedList.add(response)
                        } else {
                            updatedList.add(post)
                        }
                    }
                    updatedList
                }, loading = false))
            }

            override fun onError(e: Exception) {
                Toast.makeText(getApplication(), "Server error appeared", Toast.LENGTH_LONG).show()
                _data.postValue(_data.value?.copy(loading = false))
            }

        })
    }

    fun deleteById(id: Long) {
        _data.postValue(_data.value?.copy(loading = true))
        val new = _data.value?.posts.orEmpty().filter { it.id != id }
        repository.deleteByIdAsync(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(response: Unit) {
                _data.postValue(_data.value?.copy(posts = new, loading = false, empty = new.isEmpty()))
            }
            override fun onError(e: Exception) {
                Toast.makeText(getApplication(), "Server error appeared", Toast.LENGTH_LONG).show()
                _data.postValue(_data.value?.copy(loading = false))
            }
        })
    }

    fun refreshPosts() {
        _isRefreshing.value = true
        loadPosts(onRefresh = true)
    }
}