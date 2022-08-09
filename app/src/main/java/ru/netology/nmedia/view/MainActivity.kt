package ru.netology.nmedia.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.util.AndroidUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Создание объекта binding с наполнением из activity_main
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Создание объекта PostViewModel
        val viewModel: PostViewModel by viewModels()

        //Создание адаптера
        val adapter = PostsAdapter (
            object : OnInteractionListener {
                override fun onLike(post: Post) { viewModel.likeById(post.id) }
                override fun onRemove(post: Post) { viewModel.deleteById(post.id) }
                override fun onShare(post: Post) { viewModel.shareById(post.id) }
                override fun onEdit(post: Post) {
                    binding.clCancelEdit.visibility = View.VISIBLE
                    viewModel.edit(post)
                }
            }
        )

        binding.rvList.adapter = adapter // Адаптер передается ресайклеру

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                return@observe
            }
            with(binding.etContent) {
                requestFocus()
                setText(post.content)
            }
        }

        binding.ibCancelEdit.setOnClickListener {
            viewModel.cancel()
            binding.etContent.setText("")
            binding.etContent.clearFocus()
            AndroidUtils.hideKeyboard(binding.etContent)
            binding.clCancelEdit.visibility = View.INVISIBLE
        }

        binding.ibSave.setOnClickListener {
            with(binding.etContent) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(text.toString())
                viewModel.save()
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
            binding.clCancelEdit.visibility = View.INVISIBLE
        }

        // Подписка на обновления поля data вьюмодели. Поле data вьюмодели хранит ссылку на поле data репозитория.
        // Когда data репозитория обновляется происходит рассылка содержимого (posts) подписантам.
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts) //Адаптер принимает новый список posts и сравнивает с имеющимся для внесения изменений
        }
    }
}