package com.eibrahim.dizon.favorite.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.favorite.viewModel.FavoriteViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoriteFragment : Fragment() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteAdapter: AdapterRVFavorite
    private val viewModel: FavoriteViewModel by viewModels()

    companion object {
        fun newInstance() = FavoriteFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favourit, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewFavorite)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        favoriteAdapter = AdapterRVFavorite(emptyList())
        recyclerView.adapter = favoriteAdapter

        // Observe the LiveData from ViewModel
        viewModel.favoriteProperties.observe(viewLifecycleOwner, Observer { favoriteList ->
            favoriteAdapter.updateList(favoriteList)
        })

        return view
    }
}