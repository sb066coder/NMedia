package ru.netology.nmedia.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

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
            { viewModel.likeById(it.id) }, // Передача в адаптер колбэка ( функция типа (post: Post) -> Unit )
            { viewModel.shareById(it.id) }
        )

        binding.rvList.adapter = adapter // Адаптер передается ресайклеру

        // Подписка на обновления поля data вьюмодели. Поле data вьюмодели хранит ссылку на поле data репозитория.
        // Когда data репозитория обновляется происходит рассылка содержимого (posts) подписантам.
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts) //Адаптер принимает новый список posts и сравнивает с имеющимся для внесения изменений
        }
    }
}