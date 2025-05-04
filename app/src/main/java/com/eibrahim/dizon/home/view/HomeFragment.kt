package com.eibrahim.dizon.home.view

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
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.core.utils.UtilsFunctions
import com.eibrahim.dizon.home.view.adapters.AdapterRVOffices
import com.eibrahim.dizon.home.view.adapters.AdapterRVProperties
import com.eibrahim.dizon.home.view.adapters.AdapterRVProperties80
import com.eibrahim.dizon.home.viewModel.HomeViewModel
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var recyclerviewOffices: RecyclerView
    private lateinit var recyclerviewNew: RecyclerView
    private lateinit var recyclerviewRecommendation: RecyclerView
    private lateinit var recyclerviewSponsored: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var chatbotLayout: ImageView

    private val utils = UtilsFunctions
    private var navController: NavController? = null

    private val adapterRVProperties = AdapterRVProperties(goToDetails =  { id -> goToDetails(id) })
    private val adapterRVProperties80 = AdapterRVProperties80(goToDetails =  { id -> goToDetails(id) })

    private val adapterRVOffices = AdapterRVOffices { categoryId ->
        Log.d("HomeFragment", "Category clicked with id: $categoryId")
    }
    private val navOptions = NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
        .setPopExitAnim(R.anim.slide_out_right).build()
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
        listenerUi()
        initObservers()
    }

    private fun initUi(view: View) {
        recyclerviewOffices = view.findViewById(R.id.recyclerviewCategories)
        recyclerviewNew = view.findViewById(R.id.recyclerviewNew)
        recyclerviewRecommendation = view.findViewById(R.id.recyclerviewRecommendation)
        recyclerviewSponsored = view.findViewById(R.id.recyclerviewSponsored)
        chatbotLayout = view.findViewById(R.id.chatbot_layout)

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE

        navController =
            requireActivity().supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment)
                ?.findNavController()

    }

    private fun updateUi() {
        // Set adapters for RecyclerViews
        recyclerviewOffices.adapter = adapterRVOffices
        recyclerviewNew.adapter = adapterRVProperties
        recyclerviewRecommendation.adapter = adapterRVProperties80
        recyclerviewSponsored.adapter = adapterRVProperties80
        viewModel.loadRecommendations()

        viewModel.properties.observe(viewLifecycleOwner) { response ->

            if (response != null) {
                Log.d("Test5", response.data.toString())
            }
            if (response != null) {
                adapterRVProperties.submitList(response.data.values)
            }
            if (response != null) {
                adapterRVProperties80.submitList(response.data.values)
            }

        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }


//        adapterRVOffices.submitList(viewModel.getCate())
    }


    private fun listenerUi() {
        chatbotLayout.setOnClickListener {
            navController?.navigate(
                R.id.ChatbotFragment, null, navOptions
            )
        }

    }

    private fun initObservers() {

    }

    private fun goToDetails(id: Int) {
        val bundle = Bundle().apply {
            putInt("id", id)
        }
        Log.d("Ibra Details", id.toString())
        findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
    }


}
