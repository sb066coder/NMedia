package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
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

private val noPhoto = PhotoModel()

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
): ViewModel() {

    var postToOpen: Post? = null

    val data: Flow<PagingData<Post>> = appAuth
        .authStateFlow
        .flatMapLatest { repository.data }.flowOn(Dispatchers.Default)

    val authChanged = appAuth.authStateFlow.asLiveData()

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

    private var onScroll = false

    private lateinit var lastFailArgs: Pair<ErrorType, Any>

    init {
        loadPosts()
    }

    private fun loadPosts(onRefresh: Boolean = false) = viewModelScope.launch {
        try {
            _state.value = if (onRefresh) FeedModelState.Refreshing else FeedModelState.Loading
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
                    when(_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _state.value = FeedModelState.Idle
                } catch (e: Exception) {
                    lastFailArgs = ErrorType.SAVE to Any()
                    _state.value = FeedModelState.Error
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun edit(post: Post) {
        edited.value = post
    }

    // FIXME: Сломалось при переходе на paging
//    val newerCount: LiveData<Int> = data. {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            .catch {
//                Log.e("Error", "Network error")
//                MutableLiveData(repository.getInvisibleAmount())
//            }
//            .asLiveData(Dispatchers.Default)
//    }

    fun showNewPosts() = viewModelScope.launch {
        onScroll = true
        repository.showNewPosts()
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
            repository.deleteById(id)
        } catch (e: Exception) {
            lastFailArgs = ErrorType.DELETE to id
            _state.value = FeedModelState.Error // сообщаем об ошибке
        }
    }

    fun retry() {
        when (lastFailArgs.first) {
            ErrorType.LIKE -> likeById(
                (lastFailArgs.second as Pair<*, *>).first as Boolean,
                (lastFailArgs.second as Pair<*, *>).second as Long
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


    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

}