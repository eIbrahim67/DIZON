package com.eibrahim.dizon.propertyResults


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.main.viewModel.MainViewModel

class PropertyResultsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterRVSearchProperties

    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_property_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView = view.findViewById(R.id.propertyRecyclerView)
        adapter = AdapterRVSearchProperties(
            goToDetails = { propertyId ->
                // Handle navigation to property details if needed
                // For example: findNavController().navigate(R.id.action_to_property_details, bundleOf("propertyId" to propertyId))
            },
            onWishlistClick = { property ->
                // Handle wishlist action if needed
            }
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

    }

}