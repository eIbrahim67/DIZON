package com.eibrahim.dizon.aboutUs

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eibrahim.dizon.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class AboutUsFragment : Fragment() {

    companion object {
        fun newInstance() = AboutUsFragment()
    }

    private val viewModel: AboutUsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewModel data loading
        viewModel.loadAboutUsContent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            inflater.inflate(R.layout.fragment_about_us, container, false)
        } catch (e: Exception) {

            null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        observeViewModel()
    }

    private fun setupUI(view: View) {
        try {
            // Hide bottom navigation
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.apply {
                visibility = View.GONE
            } ?: Log.e("AboutUS","BottomNavigationView not found")

            // Set up description TextView
            view.findViewById<TextView>(R.id.appDescription)?.apply {
                // Initial placeholder text
                text = Html.fromHtml(
                    getString(R.string.about_us_placeholder),
                    Html.FROM_HTML_MODE_COMPACT
                )
            } ?: Log.e("AboutUS","Description TextView not found")
        } catch (e: Exception) {
            Log.e("AboutUS", "Error setting up UI in AboutUsFragment")
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.aboutUsContent.collect { content ->
                    view?.findViewById<TextView>(R.id.appDescription)?.apply {
                        text = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT)
                    } ?: Log.e("AboutUS","Description TextView not found during content update")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Restore bottom navigation visibility
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)?.apply {
            visibility = View.VISIBLE
        } ?: Log.e("AboutUS","BottomNavigationView not found in onDestroyView")
    }
}