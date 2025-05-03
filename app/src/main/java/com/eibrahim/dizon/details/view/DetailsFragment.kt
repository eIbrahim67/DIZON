package com.eibrahim.dizon.details.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.AuthPreferences
import com.eibrahim.dizon.details.model.PropertyDetails
import com.eibrahim.dizon.details.viewModel.DetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DetailsFragment : Fragment() {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var bottomNavigationView: BottomNavigationView
    private var propertyDetails: PropertyDetails? = null
    private lateinit var authPreferences: AuthPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        viewModel = ViewModelProvider(requireActivity())[DetailsViewModel::class.java]
        authPreferences = AuthPreferences(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide bottom navigation
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        // Initialize UI elements
        val scrollView = view.findViewById<ScrollView>(R.id.scrollView2)
        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout4)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        // Get propertyId from arguments, default to 5
        val propertyId = arguments?.getInt("propertyId") ?: 5

        // Initialize ViewPager2
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        // Fetch property details with context for caching
        viewModel.fetchPropertyDetails(propertyId, requireContext())

        // Observe property details
        viewModel.propertyDetails.observe(viewLifecycleOwner) { property ->
            property?.let {
                // Show content and hide progress bar
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                linearLayout.visibility = View.VISIBLE

                propertyDetails = it
                Log.d("DetailsFragment", "Property Details: $it")

                // Log raw image URLs to debug
                Log.d("DetailsFragment", "Raw imageUrls: ${it.imageUrls}")
                Log.d("DetailsFragment", "Image values: ${it.imageUrls?.values}")

                // Preload images with Glide
                it.imageUrls?.values?.forEach { url ->
                    Log.d("DetailsFragment", "Preloading image: $url")
                    Glide.with(requireContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .preload()
                }

                // Set up ViewPager2 with images
                val imageUrls = it.imageUrls?.values ?: listOf("placeholder")
                Log.d("DetailsFragment", "Property ID: ${it.propertyId}, Image URLs: $imageUrls")
                val adapter = ImagePagerAdapter(imageUrls)
                viewPager.adapter = adapter

                // Bind data to UI elements
                view.findViewById<TextView>(R.id.tvTitle).text = it.title
                view.findViewById<TextView>(R.id.tvPrice).text = "$${String.format("%,.2f", it.price)}"
                view.findViewById<TextView>(R.id.tvDescription).text = it.description
                view.findViewById<TextView>(R.id.tvType).text = it.propertyType
                view.findViewById<TextView>(R.id.tvRooms).text = "${it.bedrooms} Bedrooms"
                view.findViewById<TextView>(R.id.tvBeds).text = "${it.bedrooms} Beds"
                view.findViewById<TextView>(R.id.tvBaths).text = "${it.bathrooms} Baths"
                view.findViewById<TextView>(R.id.chipForSale).text = it.listingType

                // Owner info
                view.findViewById<TextView>(R.id.agent_name).text = "${it.ownerInfo.firstName} ${it.ownerInfo.lastName}"
                view.findViewById<TextView>(R.id.agent_contact).text = it.ownerInfo.phoneNumber

                // Load agent image
                Glide.with(requireContext())
                    .load(R.drawable.icon_solid_profile)
                    .placeholder(R.drawable.icon_solid_profile)
                    .into(view.findViewById(R.id.agent_profile_image))

                // Populate internal amenities
                val internalChipGroup = view.findViewById<ChipGroup>(R.id.internalAmenitiesChipGroup)
                internalChipGroup.removeAllViews()
                Log.d("DetailsFragment", "Internal amenities count: ${it.internalAmenities?.values?.size ?: 0}")
                if (it.internalAmenities?.values.isNullOrEmpty()) {
                    val chip = Chip(requireContext()).apply {
                        text = "No amenities available"
                        isClickable = false
                        setChipBackgroundColorResource(R.color.white)
                    }
                    internalChipGroup.addView(chip)
                } else {
                    it.internalAmenities?.values?.forEach { amenity ->
                        val chip = Chip(requireContext()).apply {
                            text = amenity
                            isClickable = false
                            setChipBackgroundColorResource(R.color.white)
                        }
                        internalChipGroup.addView(chip)
                    }
                }

                // Populate external amenities
                val externalChipGroup = view.findViewById<ChipGroup>(R.id.externalAmenitiesChipGroup)
                externalChipGroup.removeAllViews()
                Log.d("DetailsFragment", "External amenities count: ${it.externalAmenities?.values?.size ?: 0}")
                if (it.externalAmenities?.values.isNullOrEmpty()) {
                    val chip = Chip(requireContext()).apply {
                        text = "No amenities available"
                        isClickable = false
                        setChipBackgroundColorResource(R.color.white)
                    }
                    externalChipGroup.addView(chip)
                } else {
                    it.externalAmenities?.values?.forEach { amenity ->
                        val chip = Chip(requireContext()).apply {
                            text = amenity
                            isClickable = false
                            setChipBackgroundColorResource(R.color.white)
                        }
                        externalChipGroup.addView(chip)
                    }
                }

                // Populate accessibility amenities
                val accessibilityChipGroup = view.findViewById<ChipGroup>(R.id.accessibilityChipGroup)
                accessibilityChipGroup.removeAllViews()
                Log.d("DetailsFragment", "Accessibility amenities count: ${it.accessibilityAmenities?.values?.size ?: 0}")
                if (it.accessibilityAmenities?.values.isNullOrEmpty()) {
                    val chip = Chip(requireContext()).apply {
                        text = "No amenities available"
                        isClickable = false
                        setChipBackgroundColorResource(R.color.white)
                    }
                    accessibilityChipGroup.addView(chip)
                } else {
                    it.accessibilityAmenities?.values?.forEach { amenity ->
                        val chip = Chip(requireContext()).apply {
                            text = amenity
                            isClickable = false
                            setChipBackgroundColorResource(R.color.white)
                        }
                        accessibilityChipGroup.addView(chip)
                    }
                }

                // Handle location button click
                view.findViewById<View>(R.id.btnSeeLocation).setOnClickListener {
                    propertyDetails?.let { prop ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prop.locationUrl))
                        startActivity(intent)
                    }
                }

                // Handle contact buttons
                view.findViewById<View>(R.id.fabCall).setOnClickListener {
                    propertyDetails?.let { prop ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${prop.ownerInfo.phoneNumber}")
                        }
                        startActivity(intent)
                    }
                }
                view.findViewById<View>(R.id.message).setOnClickListener {
                    propertyDetails?.let { prop ->
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:${prop.ownerInfo.phoneNumber}")
                        }
                        startActivity(intent)
                    }
                }
                view.findViewById<View>(R.id.whatsapp).setOnClickListener {
                    propertyDetails?.let { prop ->
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/${prop.ownerInfo.phoneNumber}")
                        }
                        startActivity(intent)
                    }
                }
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error loading details", Toast.LENGTH_LONG).show()
            }
        }

        // Handle back button
        view.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}