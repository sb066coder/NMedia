package ru.netology.nmedia.model

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long): Post
    fun unLikeById(id: Long): Post

    //    fun shareById(id: Long)
    fun deleteById(id: Long)
    fun save(post: Post)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }
}