package ru.netology.nmedia.view

import ru.netology.nmedia.model.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onOpenPost(post: Post) {}
}