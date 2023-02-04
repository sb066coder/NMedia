package ru.netology.nmedia.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class FeedFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.numArg: Long by LongArg
    }

    val viewModel: PostViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
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
                    viewModel.postToOpen = post
                    findNavController().navigate(
                        R.id.action_feedFragment_to_openPostFragment
                    )
                }

                override fun onOpenImage(imageUrl: String) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_openPhotoFragment,
                        Bundle().apply { textArg = imageUrl }
                    )
                }
            }
        )

        binding.rvList.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }
/*
проверка на работоспособность без обновления при log in/out
        viewModel.authChanged.observe(viewLifecycleOwner) {
            viewModel.refreshData()
        }
*/
        // FIXME: Сломалось при переходе на paging
//        viewModel.data.observe(viewLifecycleOwner) { state ->
//            adapter.submitList(state.posts) {
//                if (viewModel.onScroll) {
//                    binding.rvList.smoothScrollToPosition(0)
//                    viewModel.onScroll = false
//                }
//            }
//            binding.tvEmptyText.isVisible = state.empty
//        }

//        viewModel.postCreated.observe(viewLifecycleOwner) {
//            adapter.submitList(viewModel.data.value?.posts)
//        }

//        viewModel.newerCount.observe(viewLifecycleOwner) {
//            binding.chNewItems.text = ("$it ${getString(R.string.fresh_posts)}")
//            binding.chNewItems.visibility = if (it > 0) View.VISIBLE else View.INVISIBLE
//        }

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

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiper.isRefreshing = it.refresh is LoadState.Loading
                        || it.append is LoadState.Loading
                        || it.prepend is LoadState.Loading
            }
        }

        binding.swiper.setOnRefreshListener {
            adapter.refresh()
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            binding.swiper.isRefreshing = it
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_editPostFragment)
        }

        viewModel.errorAppeared.observe(viewLifecycleOwner) {
            Toast.makeText(context, "Server error appeared", Toast.LENGTH_LONG).show()
        }

        binding.chNewItems.setOnClickListener { chip ->
            viewModel.showNewPosts()
            chip.visibility = View.INVISIBLE
        }

        return binding.root
    }
}