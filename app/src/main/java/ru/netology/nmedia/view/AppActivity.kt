package ru.netology.nmedia.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.view.FeedFragment.Companion.textArg

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }
            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_editPostFragment,
                Bundle().apply {
                    textArg = text
                }
            )
//            if (text.isBlank()) {
//                Snackbar.make(binding.root, R.string.error_empty_content,
//                    BaseTransientBottomBar.LENGTH_INDEFINITE
//                )
//                    .setAction(android.R.string.ok) {
//                        finish()
//                    }
//                    .show()
//                return@let
//            }
//            with(binding.tvText) {
//                this.text = this.text.toString().plus(text)
//            }
        }
    }
}