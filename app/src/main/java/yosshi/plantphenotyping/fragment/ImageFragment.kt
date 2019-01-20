package yosshi.plantphenotyping.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import yosshi.plantphenotyping.R
import yosshi.plantphenotyping.databinding.FragmentImageBinding

class ImageFragment: Fragment() {

    companion object {
        private const val RESOURCE_URL = "resource_url"

        fun newInstance(urlString: String?): Fragment =
            ImageFragment().apply {
                arguments = Bundle().apply { putString(RESOURCE_URL, urlString) }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentImageBinding.inflate(inflater, container, false)
        val urlString = arguments?.getString(RESOURCE_URL) ?: ""
        when {
            urlString.isEmpty() -> { binding.viewAnimator.displayedChild = 0 }
            else -> {
                Glide.with(this).load(urlString).into(binding.imageView)
                binding.viewAnimator.displayedChild = 1
            }
        }
        return binding.root

    }
}