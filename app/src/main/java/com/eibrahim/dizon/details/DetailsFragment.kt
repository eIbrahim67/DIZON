package com.eibrahim.dizon.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eibrahim.dizon.R
import com.eibrahim.dizon.home.model.PropertyListing
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        viewModel = ViewModelProvider(this)[DetailsViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        // Retrieve the PropertyListing passed via the arguments.
        val property = arguments?.getSerializable("property") as? PropertyListing

        property?.let { prop ->
            // Bind data to UI elements from the layout.
            view.findViewById<TextView>(R.id.tvTitle).text = prop.title
            view.findViewById<TextView>(R.id.tvPrice).text = prop.price
            view.findViewById<TextView>(R.id.tvDescription).text = prop.description

            // Load property image into the ImageView (here the id is "viewPager" in the layout)
            val imageView = view.findViewById<ImageView>(R.id.viewPager)
            Glide.with(requireContext())
                .load(prop.imageUrl)
                .centerCrop()
                .into(imageView)

            // Optionally, you could update other views (for bedrooms, bathrooms, etc.) if added in the layout.
        }
    }
}
