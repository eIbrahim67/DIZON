package com.eibrahim.dizon.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.eibrahim.dizon.R
import com.eibrahim.dizon.databinding.FragmentDetailsBinding
import com.google.android.material.chip.Chip

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var viewModel: DetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DetailsViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupAmenitiesChips()
        setupGrid()
        return binding.root
    }

    private fun setupAmenitiesChips() {
        viewModel.amenities.forEach { amenity ->
            val chip = Chip(requireContext()).apply {
                text = amenity
                isClickable = false
            }
            binding.amenitiesChipGroup.addView(chip)
        }
    }

    private fun setupGrid() {
        val gridItems = listOf(
            GridItem("Apartment", R.drawable.icon_outline_apartment),
            GridItem("120 sq m", R.drawable.icon_outline_home),
            GridItem("3 beds", R.drawable.icon_outline_hotel),
            GridItem("2 baths", R.drawable.icon_whistle) // TODO: //change icon
        )

        val gridAdapter = GridAdapter(gridItems)
        binding.gridLayoutRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.gridLayoutRecyclerView.adapter = gridAdapter
    }
}