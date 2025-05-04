package com.eibrahim.dizon.search.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

        val editTextBedrooms = view.findViewById<TextInputEditText>(R.id.BedNumTo)
        val editTextBathrooms = view.findViewById<TextInputEditText>(R.id.BathNumTo)
        val editTextMaxPrice = view.findViewById<TextInputEditText>(R.id.priceTo)
        val editTextMinPrice = view.findViewById<TextInputEditText>(R.id.priceFrom)
        val buttonApply = view.findViewById<Button>(R.id.button_apply)
        val buttonClear = view.findViewById<TextView>(R.id.button_clear)

        buttonApply.setOnClickListener {
            val filterParams = FilterParams(
                propertyType = null,
                city = null,
                governate = null,
                bedrooms = editTextBedrooms.text.toString().toIntOrNull(),
                bathrooms = editTextBathrooms.text.toString().toIntOrNull(),
                maxPrice = editTextMaxPrice.text.toString().toDoubleOrNull(),
                minPrice = editTextMinPrice.text.toString().toDoubleOrNull()
            )
            onApplyFilters?.invoke(filterParams)
            dismiss()
        }

        buttonClear.setOnClickListener {
            editTextBedrooms.text?.clear()
            editTextBathrooms.text?.clear()
            editTextMaxPrice.text?.clear()
            editTextMinPrice.text?.clear()
            onApplyFilters?.invoke(FilterParams(null, null, null, null, null, null, null))
            dismiss()
        }
    }

    fun setOnApplyFiltersListener(listener: (FilterParams) -> Unit) {
        onApplyFilters = listener
    }
}