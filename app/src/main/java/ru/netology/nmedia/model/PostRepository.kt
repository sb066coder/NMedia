package ru.netology.nmedia.model

interface PostRepository {
//    fun getAll(): List<Post>
    fun getAllAsync(callback: Callback<List<Post>>)
    fun likeByIdAsync(plusLike: Boolean, id: Long, callback: Callback<Post>)

    //    fun shareById(id: Long)
    fun deleteByIdAsync(id: Long, callback: Callback<Unit>)
    fun saveAsync(post: Post, callback: Callback<Post>)


    interface Callback<T> {
        fun onSuccess(response: T)
        fun onError(e: Exception)
    }
}