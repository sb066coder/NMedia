package ru.netology.nmedia.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityEditPostBinding

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etContent.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        binding.etContent.requestFocus()

        binding.fabPlus.setOnClickListener {
            if (binding.etContent.text.toString().isBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                val content = binding.etContent.text.toString()
                intent.putExtra(Intent.EXTRA_TEXT, content)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }
}