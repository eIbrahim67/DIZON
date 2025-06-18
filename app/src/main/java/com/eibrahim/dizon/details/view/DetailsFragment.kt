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
    private var lastErrorMessage: String? = null // Track last shown error message

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        viewModel = ViewModelProvider(this)[DetailsViewModel::class.java] // Scope to Fragment
        authPreferences = AuthPreferences(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reset last error message to avoid showing stale messages
        lastErrorMessage = null

        // Hide bottom navigation
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        // Initialize UI elements
        val scrollView = view.findViewById<ScrollView>(R.id.scrollView2)
        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout4)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val favoriteButton = view.findViewById<ImageView>(R.id.itemAddToWishlist)

        // Get propertyId from arguments
        val propertyId = arguments?.getInt("id") ?: -1 // Provide a default value if needed

        Log.d("Ibra Details", propertyId.toString())

        // Initialize ViewPager2
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        // Set initial state for favorite button (loading)
        favoriteButton.alpha = 0.5f // Dim the button while loading
        favoriteButton.setImageResource(R.drawable.ic_favorite_border)

        // Fetch property details with context for caching
        viewModel.fetchPropertyDetails(propertyId, requireContext())

        // Observe favorite status
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            favoriteButton.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .withEndAction {
                    favoriteButton.setImageResource(
                        if (isFavorite) R.drawable.ic_favorite_filled
                        else R.drawable.ic_favorite_border
                    )
                    favoriteButton.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                    favoriteButton.alpha = 1.0f
                }
                .start()
        }

        // Handle favorite button click
        favoriteButton.setOnClickListener {
            viewModel.toggleFavorite(propertyId)
        }

        // Observe property details
        viewModel.propertyDetails.observe(viewLifecycleOwner) { property ->
            if (property != null && property.propertyId == propertyId) {
                progressBar.visibility = View.GONE
                scrollView.visibility = View.VISIBLE
                linearLayout.visibility = View.VISIBLE

                propertyDetails = property

                Log.d("DetailsFragment", "Property Details: $property")

                property.imageUrls?.values?.forEach { url ->
                    Log.d("DetailsFragment", "Preloading image: $url")
                    Glide.with(requireContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .preload()
                }

                val imageUrls = property.imageUrls?.values ?: listOf("placeholder")
                Log.d("DetailsFragment", "Property ID: ${property.propertyId}, Image URLs: $imageUrls")
                val adapter = ImagePagerAdapter(imageUrls)
                viewPager.adapter = adapter

                view.findViewById<TextView>(R.id.tvTitle).text = property.title
                view.findViewById<TextView>(R.id.tvPrice).text = String.format("%.0f LE", property.price)
                view.findViewById<TextView>(R.id.tvDescription).text = property.description
                view.findViewById<TextView>(R.id.tvType).text = property.propertyType
                view.findViewById<TextView>(R.id.tvRooms).text = "${property.bedrooms} Bedrooms"
                view.findViewById<TextView>(R.id.tvBeds).text = "${property.bedrooms} Beds"
                view.findViewById<TextView>(R.id.tvBaths).text = "${property.bathrooms} Baths"
                view.findViewById<TextView>(R.id.chipForSale).text = property.listingType

                view.findViewById<TextView>(R.id.agent_name).text = "${property.ownerInfo.firstName} ${property.ownerInfo.lastName}"
                view.findViewById<TextView>(R.id.agent_contact).text = property.ownerInfo.phoneNumber

                Glide.with(requireContext())
                    .load(R.drawable.icon_solid_profile)
                    .placeholder(R.drawable.icon_solid_profile)
                    .into(view.findViewById(R.id.agent_profile_image))

                val internalChipGroup = view.findViewById<ChipGroup>(R.id.internalAmenitiesChipGroup)
                internalChipGroup.removeAllViews()
                Log.d("DetailsFragment", "Internal amenities count: ${property.internalAmenities?.values?.size ?: 0}")
                if (property.internalAmenities?.values.isNullOrEmpty()) {
                    val chip = Chip(requireContext()).apply {
                        text = "No amenities available"
                        isClickable = false
                        setChipBackgroundColorResource(R.color.white)
                    }
                    internalChipGroup.addView(chip)
                } else {
                    property.internalAmenities?.values?.forEach { amenity ->
                        val chip = Chip(requireContext()).apply {
                            text = amenity
                            isClickable = false
                            setChipBackgroundColorResource(R.color.white)
                        }
                        internalChipGroup.addView(chip)
                    }
                }

                val externalChipGroup = view.findViewById<ChipGroup>(R.id.externalAmenitiesChipGroup)
                externalChipGroup.removeAllViews()
                Log.d("DetailsFragment", "External amenities count: ${property.externalAmenities?.values?.size ?: 0}")
                if (property.externalAmenities?.values.isNullOrEmpty()) {
                    val chip = Chip(requireContext()).apply {
                        text = "No amenities available"
                        isClickable = false
                        setChipBackgroundColorResource(R.color.white)
                    }
                    externalChipGroup.addView(chip)
                } else {
                    property.externalAmenities?.values?.forEach { amenity ->
                        val chip = Chip(requireContext()).apply {
                            text = amenity
                            isClickable = false
                            setChipBackgroundColorResource(R.color.white)
                        }
                        externalChipGroup.addView(chip)
                    }
                }

                val accessibilityChipGroup = view.findViewById<ChipGroup>(R.id.accessibilityChipGroup)
                accessibilityChipGroup.removeAllViews()
                Log.d("DetailsFragment", "Accessibility amenities count: ${property.accessibilityAmenities?.values?.size ?: 0}")
                if (property.accessibilityAmenities?.values.isNullOrEmpty()) {
                    val chip = Chip(requireContext()).apply {
                        text = "No amenities available"
                        isClickable = false
                        setChipBackgroundColorResource(R.color.white)
                    }
                    accessibilityChipGroup.addView(chip)
                } else {
                    property.accessibilityAmenities?.values?.forEach { amenity ->
                        val chip = Chip(requireContext()).apply {
                            text = amenity
                            isClickable = false
                            setChipBackgroundColorResource(R.color.white)
                        }
                        accessibilityChipGroup.addView(chip)
                    }
                }

                view.findViewById<View>(R.id.btnSeeLocation).setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(property.locationUrl))
                    startActivity(intent)
                }

                view.findViewById<View>(R.id.fabCall).setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${property.ownerInfo.phoneNumber}")
                    }
                    startActivity(intent)
                }
                view.findViewById<View>(R.id.message).setOnClickListener {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("smsto:${property.ownerInfo.phoneNumber}")
                    }
                    startActivity(intent)
                }
                view.findViewById<View>(R.id.whatsapp).setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/${property.ownerInfo.phoneNumber}")
                    }
                    startActivity(intent)
                }
            } else {
                progressBar.visibility = View.VISIBLE
                scrollView.visibility = View.GONE
                linearLayout.visibility = View.GONE
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Only show toast if the message is new and relevant
                if (it != lastErrorMessage && it.isNotEmpty()) {
                    progressBar.visibility = View.GONE
                    if (it == "Property added to favorites" || it == "Property removed from favorites") {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_LONG).show()
                    }
                    lastErrorMessage = it
                }
            }
        }

        // Handle back button
        view.findViewById<ImageView>(R.id.backButton).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}