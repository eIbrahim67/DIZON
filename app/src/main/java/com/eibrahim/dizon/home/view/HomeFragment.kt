package com.eibrahim.dizon.home.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.core.remote.model.Property
import com.eibrahim.dizon.core.remote.model.Category
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.core.utils.UtilsFunctions
import com.eibrahim.dizon.home.view.adapters.AdapterRVCategories
import com.eibrahim.dizon.home.view.adapters.AdapterRVProps
import com.eibrahim.dizon.home.viewModel.HomeViewModel
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var recyclerviewCategories: RecyclerView
    private lateinit var recyclerviewProps: RecyclerView
    private lateinit var currentDate: TextView
    private lateinit var textViewHello: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    private val utils = UtilsFunctions

    private val adapterRVProps = AdapterRVProps { id -> goToProp(id) }
    private val adapterRVCategories = AdapterRVCategories { categoryId ->
        Log.d("HomeFragment", "Category clicked with id: $categoryId")
        // Optionally navigate to a search or filter page based on categoryId
    }

    private val viewModel: HomeViewModel by viewModels()
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi(view)
        updateUi()
        initObservers()
    }

    private fun initUi(view: View) {
        recyclerviewCategories = view.findViewById(R.id.recyclerviewCategories)
        recyclerviewProps = view.findViewById(R.id.recyclerviewProps)
        textViewHello = view.findViewById(R.id.textViewHello)
        currentDate = view.findViewById(R.id.currentDate)

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun updateUi() {
        // Set adapters for RecyclerViews
        recyclerviewCategories.adapter = adapterRVCategories
        recyclerviewProps.adapter = adapterRVProps

        // Submit dummy data to adapters
        adapterRVProps.submitList(getDummyProperties())
        adapterRVCategories.submitList(getDummyCategories())
    }

    private fun initObservers() {
        viewModel.getCurrentDate()
        viewModel.currentDate.observe(viewLifecycleOwner) { date ->


            when (date) {
                is Response.Loading -> {}

                is Response.Success -> {
                    currentDate.text = date.data
                }

                is Response.Failure -> {
                    utils.createFailureResponse(date, requireContext())
                }
            }

        }

        viewModel.getHelloSate()
        viewModel.helloSate.observe(viewLifecycleOwner) { date ->


            when (date) {
                is Response.Loading -> {}

                is Response.Success -> {
                    textViewHello.text = date.data
                }

                is Response.Failure -> {
                    utils.createFailureResponse(date, requireContext())
                }
            }

        }
    }

    private fun goToProp(id: Int) {
        // Handle property item click event
        Log.d("HomeFragment", "Property clicked with id: $id")
        // Navigation to property detail can be implemented here
    }

    // Generate dummy property data
    private fun getDummyProperties(): List<Property> {
        return listOf(
            Property(
                id = 1,
                title = "Modern Apartment",
                description = "A modern apartment in the heart of the city.",
                address = "123 Main St",
                city = "New York",
                state = "NY",
                zipCode = "10001",
                country = "USA",
                latitude = 40.7128,
                longitude = -74.0060,
                price = 450000.0,
                area = 850.0,
                bedrooms = 2,
                bathrooms = 2,
                floors = 1,
                images = listOf(url),
                createdAt = System.currentTimeMillis(),
                updatedAt = null
            ),
            Property(
                id = 2,
                title = "Cozy Cottage",
                description = "A charming cottage with a beautiful garden.",
                address = "456 Elm St",
                city = "Boston",
                state = "MA",
                zipCode = "02118",
                country = "USA",
                latitude = 42.3601,
                longitude = -71.0589,
                price = 350000.0,
                area = 1200.0,
                bedrooms = 3,
                bathrooms = 2,
                floors = 2,
                images = listOf(url),
                createdAt = System.currentTimeMillis(),
                updatedAt = null
            ),
            Property(
                id = 3,
                title = "Luxury Villa",
                description = "A luxurious villa with state-of-the-art amenities.",
                address = "789 Oak Ave",
                city = "Los Angeles",
                state = "CA",
                zipCode = "90001",
                country = "USA",
                latitude = 34.0522,
                longitude = -118.2437,
                price = 1200000.0,
                area = 3500.0,
                bedrooms = 5,
                bathrooms = 4,
                floors = 2,
                images = listOf(url),
                createdAt = System.currentTimeMillis(),
                updatedAt = null
            )
        )
    }

    // Generate dummy category data
    private fun getDummyCategories(): List<Category> {
        return listOf(
            Category(
                id = 1,
                name = "Apartments",
                description = "Find modern apartments for rent or sale.",
                iconUrl = "https://images.ctfassets.net/pg6xj64qk0kh/2uUvn1x29JUfeHVuzakyhJ/9b540ac51547aa6954433e6e19db76fb/camden-atlantic-apartments-plantation-fl-pool-at-dusk-view.JPG"
            ),
            Category(
                id = 2,
                name = "Houses",
                description = "Explore beautiful houses with spacious interiors.",
                iconUrl = url2
            ),
            Category(
                id = 3,
                name = "Commercial",
                description = "Discover commercial properties for business needs.",
                iconUrl = url2
            ),
            Category(
                id = 4,
                name = "Luxury",
                description = "Browse luxury properties with premium amenities.",
                iconUrl = "https://topluxuryproperty.com//uploadfile/gallery/damac-islands-0_3_638615954946025321_820465_.jpg"
            )
        )
    }

    val url = "https://firebasestorage.googleapis.com/v0/b/gympro-eibrahim.appspot.com/o/Morehead-Low-Country-1.avif?alt=media&token=c00e6c97-7aff-4357-8fc0-cad4ca407c2a"
    val url2 = "https://firebasestorage.googleapis.com/v0/b/gympro-eibrahim.appspot.com/o/custom-home-builder-toronto.jpg?alt=media&token=47a2627b-c7cb-43ca-9f6f-d4579faf7619"
}
