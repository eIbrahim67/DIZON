package com.eibrahim.dizon.search.presentation.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.eibrahim.dizon.search.presentation.viewModel.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchFragment : Fragment() {

    private lateinit var searchType: EditText
    private lateinit var searchCity: EditText
    private lateinit var searchBtn: Button
    private lateinit var recyclerviewSearch: RecyclerView
    private lateinit var filter_layout: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView

    private val viewModel: SearchViewModel by viewModels()
    private val adapterRVProperties = AdapterRVSearch(goToDetails =  { id -> goToDetails(id) })

    private val sharedViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi(view)
        updateUi()
        listenerUi()
        initObservers()
    }

    private fun initUi(view: View) {
        searchType = view.findViewById(R.id.search_type_text)
        searchCity = view.findViewById(R.id.search_city_text)
        filter_layout = view.findViewById(R.id.filter_layout)
        searchBtn = view.findViewById(R.id.search_btn)
        recyclerviewSearch = view.findViewById(R.id.recyclerviewSearch)
        recyclerviewSearch.adapter = adapterRVProperties

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE

    }

    private fun initObservers() {
        viewModel.properties.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.d("SearchFragment", "Properties: ${response.data}")
                adapterRVProperties.submitList(response.data.values)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            recyclerviewSearch.visibility = if (isLoading) View.GONE else View.VISIBLE
            // Optionally show a ProgressBar here
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                // Optionally show error message in UI
                Log.e("SearchFragment", "Error: $errorMessage")
            }
        }

        sharedViewModel.searchType.observe(viewLifecycleOwner){ data ->

            searchType.setText(data)

        }
    }

    private fun listenerUi() {
        searchType.addTextChangedListener {
            searchBtn.visibility = View.VISIBLE
            viewModel.updateFilterParams(propertyType = it.toString().takeIf { it.isNotBlank() })
            viewModel.loadAllProperties()
        }

        searchCity.addTextChangedListener {
            searchBtn.visibility = View.VISIBLE
            viewModel.updateFilterParams(city = it.toString().takeIf { it.isNotBlank() })
            viewModel.loadAllProperties()
        }

        searchBtn.setOnClickListener {
            searchBtn.visibility = View.GONE
            viewModel.loadAllProperties()
        }

        filter_layout.setOnClickListener {
            val btmSh = FilterBottomSheetFragment()
            btmSh.setOnApplyFiltersListener { filterParams ->
                viewModel.updateFilterParams(
                    propertyType = searchType.text.toString(),
                    city = searchCity.text.toString(),
                    governate = filterParams.governate,
                    bedrooms = filterParams.bedrooms,
                    bathrooms = filterParams.bathrooms,
                    maxPrice = filterParams.maxPrice,
                    minPrice = filterParams.minPrice
                )
                viewModel.loadAllProperties()
            }
            btmSh.show(requireActivity().supportFragmentManager, "FilterBottomSheet")
        }
    }

    private fun updateUi() {
        searchBtn.visibility = View.GONE
    }

    private fun goToDetails(id: Int) {
        val bundle = Bundle().apply {
            putInt("id", id)
        }
        Log.d("Ibra Details", id.toString())
        findNavController().navigate(R.id.action_searchFragment_to_detailsFragment, bundle)
    }

}