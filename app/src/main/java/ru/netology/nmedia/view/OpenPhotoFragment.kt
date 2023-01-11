package ru.netology.nmedia.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.OpenPhotoBinding
import ru.netology.nmedia.view.FeedFragment.Companion.textArg

class OpenPhotoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = OpenPhotoBinding.inflate(
            inflater,
            container,
            false
        )

        val url = arguments?.textArg

        Glide.with(binding.ivImage)
            .load(url)
            .timeout(10_000)
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_cancel_24)
            .fitCenter()
            .into(binding.ivImage)

        return binding.root
    }

}