package ru.netology.nmedia.model

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)

sealed interface FeedModelState {
    object Idle : FeedModelState
    object Error : FeedModelState
    object Loading : FeedModelState
    object Refreshing : FeedModelState
}