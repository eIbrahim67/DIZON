package com.eibrahim.dizon.search.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var onApplyFilters: ((FilterParams) -> Unit)? = null

    data class FilterParams(
        val propertyType: String?,
        val city: String?,
        val governate: String?,
        val bedrooms: Int?,
        val bathrooms: Int?,
        val maxPrice: Double?,
        val minPrice: Double?
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val priceFrom = view.findViewById<TextInputEditText>(R.id.priceFrom)
        val priceTo = view.findViewById<TextInputEditText>(R.id.priceTo)

        val bedNumFrom = view.findViewById<TextInputEditText>(R.id.BedNumFrom)
        val bedNumTo = view.findViewById<TextInputEditText>(R.id.BedNumTo)

        val bathNumFrom = view.findViewById<TextInputEditText>(R.id.BathNumFrom)
        val bathNumTo = view.findViewById<TextInputEditText>(R.id.BathNumTo)

        val buttonForSale = view.findViewById<Button>(R.id.button1)
        val buttonForRent = view.findViewById<Button>(R.id.button2)

        val propertyTypeRecyclerView = view.findViewById<RecyclerView>(R.id.rvPropertyTypeFilter)

        val internalAmenitiesRecyclerView =
            view.findViewById<RecyclerView>(R.id.rvInternalAmenitiesFilter)
        val externalAmenitiesRecyclerView =
            view.findViewById<RecyclerView>(R.id.rvExternalAmenitiesFilter)
        val accessibilityAmenitiesRecyclerView =
            view.findViewById<RecyclerView>(R.id.rvAccessibilityAmenitiesFilter)

        val backButton = view.findViewById<ImageView>(R.id.backFilterBtn)
        val applyFilter =
            view.findViewById<TextView>(R.id.applyFilter) // Note: Add `android:id="@+id/text_reset"` to the Reset TextView if missing.


        applyFilter.setOnClickListener {
            val filterParams = FilterParams(
                propertyType = null, // RecyclerView selection logic should be handled separately
                city = null,         // You don't currently have these inputs in the new layout
                governate = null,
                bedrooms = bedNumFrom.text.toString()
                    .toIntOrNull(), // Only From is captured; adjust logic if needed
                bathrooms = bathNumFrom.text.toString().toIntOrNull(),
                maxPrice = priceTo.text.toString().toDoubleOrNull(),
                minPrice = priceFrom.text.toString().toDoubleOrNull()
            )
            onApplyFilters?.invoke(filterParams)
            dismiss()
        }

//        buttonClear.setOnClickListener {
//            priceFrom.text?.clear()
//            priceTo.text?.clear()
//            bedNumFrom.text?.clear()
//            bedNumTo.text?.clear()
//            bathNumFrom.text?.clear()
//            bathNumTo.text?.clear()

        // You may also want to reset RecyclerViews and buttons if applicable

        onApplyFilters?.invoke(FilterParams(null, null, null, null, null, null, null))
        dismiss()
    }

    fun setOnApplyFiltersListener(listener: (FilterParams) -> Unit) {
        onApplyFilters = listener
    }
}

