package com.eibrahim.dizon.home.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.home.viewModel.HomeViewModel
import com.eibrahim.dizon.R
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {


    private lateinit var recyclerviewCategories: RecyclerView
    private lateinit var recyclerviewFeaturePlan: RecyclerView
    private lateinit var recyclerviewOurTrainers: RecyclerView
    private lateinit var recyclerviewOthersWorkout: RecyclerView
    private lateinit var currentDate: TextView
    private lateinit var textViewHello: TextView

    private lateinit var bottomNavigationView: BottomNavigationView

//    private val adapterRVFeaturedPlans = AdapterRVFeaturedPlans() { id -> goToTrainPlan(id) }
//    private val adapterRVCategories = AdapterRVCategories()
//
//    private val utils = UtilsFunctions

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

    private fun updateUi() {
//        recyclerviewCategories.adapter = adapterRVCategories
//        recyclerviewFeaturePlan.adapter = adapterRVFeaturedPlans

    }

    private fun initUi(view: View) {
        recyclerviewCategories = view.findViewById(R.id.recyclerviewCategories)
        recyclerviewFeaturePlan = view.findViewById(R.id.recyclerviewFeaturePlan)

        textViewHello = view.findViewById(R.id.textViewHello)
        currentDate = view.findViewById(R.id.currentDate)

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun initObservers() {


    }


}