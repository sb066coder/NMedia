package ru.netology.nmedia.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentOpenPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.ViewUtils
import ru.netology.nmedia.view.FeedFragment.Companion.numArg
import ru.netology.nmedia.viewmodel.PostViewModel

class OpenPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
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

        viewModel.data.observe(viewLifecycleOwner) {
            try {
                val post: Post = viewModel.data.value!!.first { it.id == arguments?.numArg }

                binding.apply {
                    tvAuthor.text = post.author
                    tvPublished.text = post.published
                    tvContent.text = post.content
                    mbLike.isChecked = post.likedByMe
                    mbLike.text = ViewUtils.formattedNumber(post.likes)
                    mbShare.text = ViewUtils.formattedNumber(post.shares)
                    mbWatch.text = ViewUtils.formattedNumber(post.watches)
                    ivVideoContent.visibility =
                        if (post.videoContent != null) View.VISIBLE else View.GONE
    //            fillInPostData(this)
                    mbLike.setOnClickListener { viewModel.likeById(post.id) }
                    mbShare.setOnClickListener {
                        viewModel.shareById(post.id)
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
                                        findNavController().popBackStack()
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
                    ivVideoContent.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoContent))
                        startActivity(intent)
                    }
                }
            } catch (e: NoSuchElementException) {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }
}