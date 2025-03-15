package com.eibrahim.dizon.edit_profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eibrahim.dizon.R
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout

class EditProfileFragment : Fragment() {

    companion object {
        fun newInstance() = EditProfileFragment()
    }

    private val viewModel: EditProfileViewModel by viewModels()

    private lateinit var cityDropdown: AutoCompleteTextView
    private lateinit var cityDropdownLayout: TextInputLayout




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        cityDropdown = view.findViewById(R.id.cityDropdown)
        cityDropdownLayout = view.findViewById(R.id.cityDropdownLayout)

        setupCityDropdown()

        // Example of accessing ViewModel data/methods
        viewModel.loadData()
    }

    private fun setupCityDropdown() {
        // Get city list from ViewModel
        val cityList = viewModel.getCityList()

        // Create the ArrayAdapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            cityList
        )

        // Set the adapter on the AutoCompleteTextView
        cityDropdown.setAdapter(adapter)

        // Set a threshold to start showing suggestions
        cityDropdown.threshold = 1

        // Handle item selection
        cityDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = adapter.getItem(position)
            viewModel.onCitySelected(selectedCity)
        }
    }

}