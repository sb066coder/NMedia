package ru.netology.nmedia.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
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

enum class ErrorType {
    LIKE, DELETE, LOAD, SAVE
}

class PostViewModel(application: Application): AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryServerImpl(AppDb.getInstance(application).postDao())

    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(it, it.isEmpty())
    }

    private val _state = SingleLiveEvent<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing

    private val _errorAppeared = SingleLiveEvent<Unit>()
    val errorAppeared: LiveData<Unit>
        get() = _errorAppeared

    private val scope = MainScope()

    private lateinit var lastFailArgs: Pair<ErrorType, Any>

    init {
        loadPosts()
    }

    private fun loadPosts(onRefresh: Boolean = false) = viewModelScope.launch {
        try {
            _state.value = if (onRefresh) FeedModelState.Refreshing else FeedModelState.Loading
            repository.getAll()
            _state.value = FeedModelState.Idle
        } catch (e: Exception) {
            lastFailArgs = ErrorType.LOAD to Any()
            _state.value = FeedModelState.Error
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _state.value = FeedModelState.Idle
                } catch (e: Exception) {
                    lastFailArgs = ErrorType.SAVE to Any()
                    _state.value = FeedModelState.Error
                }
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

    fun likeById(plusLike: Boolean, id: Long) = viewModelScope.launch {
        try {
            repository.likeById(plusLike, id)
        } catch (e: Exception) {
            lastFailArgs = ErrorType.LIKE to (plusLike to id)
            _state.value = FeedModelState.Error
        }
    }

    fun deleteById(id: Long) = viewModelScope.launch {
        try {
            Log.i("AAA", "vm try delete")
            repository.deleteById(id)
        } catch (e: Exception) {
            Log.i("AAA", "vm catch exception delete")
            lastFailArgs = Pair(ErrorType.DELETE, id)
            _state.value = FeedModelState.Error // сообщаем об ошибке
        }
    }

    fun retry() {
        when (lastFailArgs.first) {
            ErrorType.LIKE -> likeById(
                (lastFailArgs.second as Pair<Boolean, Long>).first,
                (lastFailArgs.second as Pair<Boolean, Long>).second
            )
            ErrorType.DELETE -> deleteById(lastFailArgs.second as Long)
            ErrorType.LOAD -> loadPosts()
            ErrorType.SAVE -> save()
        }
    }

    fun refreshPosts() {
        _state.value = FeedModelState.Refreshing
        loadPosts(onRefresh = true)
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}