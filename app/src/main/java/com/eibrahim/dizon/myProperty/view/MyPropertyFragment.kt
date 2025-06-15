package com.eibrahim.dizon.myProperty.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.myProperty.view.adapter.AdapterRVMyProperties
import com.eibrahim.dizon.myProperty.viewmodel.MyPropertyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPropertyFragment : Fragment() {

    companion object {
        fun newInstance() = MyPropertyFragment()
    }

    private val viewModel: MyPropertyViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_property, container, false)
    }

    private val adapterRvMyProperties = AdapterRVMyProperties(goToDetails = { id -> goToDetails(id) })

    private lateinit var recyclerview: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        recyclerview = view.findViewById(R.id.recyclerviewMy)
        recyclerview.adapter = adapterRvMyProperties

        viewModel.loadMyProperties()

        viewModel.properties.observe(viewLifecycleOwner) { response ->
            if (response == null) {
                Log.d("Test5", "Response is null")
            } else {
                adapterRvMyProperties.submitList(response.values)
            }
        }
    }
    private fun goToDetails(id: Int) {
        val bundle = Bundle().apply {
            putInt("id", id)
        }
        Log.d("Ibra Details", id.toString())
        findNavController().navigate(R.id.action_myPropertyFragment_to_detailsFragment, bundle)
    }

    private lateinit var bottomNavigationView: BottomNavigationView
}