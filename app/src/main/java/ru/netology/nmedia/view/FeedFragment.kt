package ru.netology.nmedia.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.view.EditPostFragment.Companion.textArg
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Создание объекта binding с наполнением из activity_main
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )
//        setContentView(binding.root)


        // Создание контракта
        val editPostActivity = registerForActivityResult(EditPostResultContract()) { result ->
            if(result == null) {
                viewModel.cancel()
                return@registerForActivityResult
            }
            viewModel.changeContent(result)
            viewModel.save()
        }

        // Создание адаптера
        val adapter = PostsAdapter (
            object : OnInteractionListener {
                override fun onLike(post: Post) { viewModel.likeById(post.id) }
                override fun onRemove(post: Post) { viewModel.deleteById(post.id) }
                override fun onShare(post: Post) {
                    viewModel.shareById(post.id)
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
                    findNavController().navigate(R.id.action_feedFragment_to_editPostFragment)
                }

                override fun onViewVideo(post: Post) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoContent))
                    startActivity(intent)
                }
            }
        )

        binding.rvList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts) //Адаптер принимает новый список posts и сравнивает с имеющимся для внесения изменений
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_editPostFragment)
        }

        return binding.root
    }
}