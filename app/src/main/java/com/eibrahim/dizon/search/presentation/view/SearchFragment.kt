package com.eibrahim.dizon.search.presentation.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.home.view.adapters.AdapterRVProperties
import com.eibrahim.dizon.home.view.adapters.AdapterRVProperties80
import com.eibrahim.dizon.search.presentation.viewModel.SearchViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchFragment : Fragment() {

    private lateinit var searchText: EditText
    private lateinit var searchBtn: Button
    private lateinit var recyclerviewSearch: RecyclerView
    private lateinit var filter_layout: ImageView

    private val viewModel: SearchViewModel by viewModels()

    private val adapterRVProperties = AdapterRVProperties {
        Toast.makeText(requireContext(), "clicked", Toast.LENGTH_SHORT).show()
    }

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
        searchText = view.findViewById(R.id.search_text)
        filter_layout = view.findViewById(R.id.filter_layout)
        searchBtn = view.findViewById(R.id.search_btn)
        recyclerviewSearch = view.findViewById(R.id.recyclerviewSearch)
        recyclerviewSearch.adapter = adapterRVProperties




    }

    private fun initObservers() {

    }

    private fun listenerUi() {

        searchText.addTextChangedListener {
            searchBtn.visibility = View.VISIBLE
        }

        searchBtn.setOnClickListener {
            searchBtn.visibility = View.GONE
        }

        filter_layout.setOnClickListener {
            val btmSh = BottomSheetDialogFragment(R.layout.bottomsheet_filter)
            btmSh.show(requireActivity().supportFragmentManager, "FilterBottomSheet")

        }

    }

    private fun updateUi() {
        searchBtn.visibility = View.GONE
        adapterRVProperties.submitList(viewModel.getList())
    }

}