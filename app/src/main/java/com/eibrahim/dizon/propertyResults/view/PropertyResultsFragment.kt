package com.eibrahim.dizon.propertyResults.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.home.viewModel.HomeViewModel
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.eibrahim.dizon.propertyResults.viewModel.AllResultViewModel
import com.eibrahim.dizon.search.data.Property

class PropertyResultsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRVAllResult

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: AllResultViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_property_results, container, false)
    }

    private lateinit var backFromResult: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        backFromResult = view.findViewById(R.id.backFromResult)

        backFromResult.setOnClickListener {

            findNavController().popBackStack()

        }

        recyclerView = view.findViewById(R.id.propertyRecyclerView)
        adapter = AdapterRVAllResult(
            goToDetails = { id -> goToDetails(id) },
            onWishlistClick = { property -> toggleFavorite(property) }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@PropertyResultsFragment.adapter
        }

        sharedViewModel.properties.observe(viewLifecycleOwner) { response ->

            if (response != null) {
                adapter.submitList(response.data.values)
            }
        }

        viewModel.fetchFavorites() // fetch at start

        viewModel.favoriteIds.observe(viewLifecycleOwner) { favIds ->
            adapter.setFavorites(favIds)
            adapter.setFavorites(favIds)
        }

    }

    private fun goToDetails(id: Int) {
        val bundle = Bundle().apply {
            putInt("id", id)
        }
        Log.d("Ibra Details", id.toString())
        findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
    }

    private fun toggleFavorite(property: Property) {
        viewModel.toggleFavorite(property)
    }

}