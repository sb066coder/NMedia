package ru.netology.nmedia.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.numArg: Long by LongArg
    }

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter (
            object : OnInteractionListener {
                override fun onLike(post: Post) {  viewModel.likeById(!post.likedByMe, post.id) }
                override fun onRemove(post: Post) { viewModel.deleteById(post.id) }
                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }
                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_feedFragment_to_editPostFragment,
                        Bundle().apply { textArg = post.content }
                    )
                }

                override fun onOpenPost(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_openPostFragment,
                        Bundle().apply { numArg = post.id }
                    )
                }
            }
        )

        binding.rvList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.tvEmptyText.isVisible = state.empty
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state is FeedModelState.Loading
            binding.swiper.isRefreshing = state is FeedModelState.Refreshing
            if (state is FeedModelState.Error) {
                Snackbar.make(
                    binding.root,
                    R.string.retry_text,
                    Snackbar.LENGTH_SHORT
                ).setAction(R.string.retry) {
                    viewModel.retry()
                }
                    .show()
            }
        }

        binding.swiper.setOnRefreshListener {
            viewModel.refreshPosts()
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            binding.swiper.isRefreshing = it
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_editPostFragment)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.data.value?.posts)
        }

        viewModel.errorAppeared.observe(viewLifecycleOwner) {
            Toast.makeText(context, "Server error appeared", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }
}