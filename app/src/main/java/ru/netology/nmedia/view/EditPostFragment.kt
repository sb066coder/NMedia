package ru.netology.nmedia.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
const val DRAFT_KEY = "draft"
class EditPostFragment : Fragment() {

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
        val binding = FragmentEditPostBinding.inflate(
            inflater,
            container,
            false
        )

        val sPref = requireContext().getSharedPreferences("newPostContent", Context.MODE_PRIVATE)

        if (arguments == null) {
            binding.etContent.setText(sPref.getString(DRAFT_KEY, ""))
        } else {
            arguments?.textArg.let( binding.etContent::setText )
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                sPref.edit().apply {
                        putString(DRAFT_KEY, binding.etContent.text.toString())
                        apply()
                    }
                findNavController().navigateUp()
            }
        })

        binding.fabPlus.setOnClickListener {
            viewModel.changeContent(binding.etContent.text.toString())
            viewModel.save()
            sPref.edit().apply() {
                clear()
                apply()
            }
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        return binding.root
    }
}