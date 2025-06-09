package com.eibrahim.dizon.favorite.view

import android.R.anim
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.AuthPreferences
import com.eibrahim.dizon.favorite.viewModel.FavoriteViewModel
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class FavoriteFragment : Fragment() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteAdapter: AdapterRVFavorite
    private lateinit var authPreferences: AuthPreferences
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: FavoriteViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FavoriteViewModel(mainViewModel) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favourit, container, false)
        authPreferences = AuthPreferences(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show bottom navigation
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewFavorite)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with click listeners for favorite button and property details
        favoriteAdapter = AdapterRVFavorite(
            emptyList(),
            onFavoriteClick = { propertyId ->
                viewModel.removeFavorite(propertyId)
            },
            onItemClick = { propertyId ->
                val bundle = Bundle().apply {
                    putInt("id", propertyId)
                }
                // Apply custom animations for navigation
                val navOptions = navOptions {
                    anim {
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_right
                    }
                }
                findNavController().navigate(
                    R.id.action_action_wishlist_to_detailsFragment,
                    bundle,
                    navOptions
                )
            }
        )
        recyclerView.adapter = favoriteAdapter

        // Fetch favorites
        viewModel.fetchFavorites()

        // Observe favorite properties
        viewModel.favoriteProperties.observe(viewLifecycleOwner) { favoriteList ->
            if (favoriteList.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
            } else {
                recyclerView.visibility = View.VISIBLE
                favoriteAdapter.updateList(favoriteList)
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e("FavoriteFragment", "Error: $it")
                if (it.contains("Unauthorized")) {
                    Toast.makeText(requireContext(), "Session expired. Log in again", Toast.LENGTH_LONG).show()
                    authPreferences.clearToken()
                } else if (it == "Property removed from favorites") {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError() // Clear error to prevent repeated display
                } else {
                    Snackbar.make(view, "Error: $it", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            viewModel.undoRemoveFavorite()
                        }
                        .show()
                    viewModel.clearError() // Clear error to prevent repeated display
                }
                if (it != "Property removed from favorites") {
                    recyclerView.visibility = View.GONE
                }
            }
        }
    }
}