package ru.netology.nmedia.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentOpenPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.ViewUtils
import ru.netology.nmedia.adapter.PostViewHolder.Companion.G_BASE_URL
import ru.netology.nmedia.viewmodel.PostViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class OpenPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOpenPostBinding.inflate(
            inflater,
            container,
            false
        )


        val post: Post = viewModel.postToOpen ?: throw RuntimeException()
        viewModel.postToOpen = null
        binding.apply {
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = post.content
            mbLike.isChecked = post.likedByMe
            mbLike.text = ViewUtils.formattedNumber(post.likes)
            mbShare.text = ViewUtils.formattedNumber(post.shares)
            mbWatch.text = ViewUtils.formattedNumber(post.watches)
            mbLike.setOnClickListener { viewModel.likeById(!post.likedByMe, post.id) }
            mbShare.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            mbMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                viewModel.deleteById(post.id)
                                true
                            }
                            R.id.edit -> {
                                viewModel.edit(post)
                                findNavController().navigate(
                                    R.id.action_openPostFragment_to_editPostFragment,
                                    Bundle().apply { textArg = post.content }
                                )
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            val url = "${G_BASE_URL}/avatars/${post.authorAvatar}"
            ivAvatar.loadCircleCrop(url)

            if (post.attachment != null) {
                ivContent.visibility = View.VISIBLE
                ivContent.load("$G_BASE_URL/media/${post.attachment.url}")
            } else {
                ivContent.visibility = View.GONE
            }

            ivContent.setOnClickListener {
                findNavController().navigate(
                    R.id.action_openPostFragment_to_openPhotoFragment,
                    Bundle().apply { textArg = "$G_BASE_URL/media/${post.attachment?.url}" }
                )
            }
        }

        viewModel.errorAppeared.observe(viewLifecycleOwner) {
            Toast.makeText(context, "Server error appeared", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }
}