package com.eibrahim.dizon.auth.register.view

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import java.util.Calendar

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val etDate = view.findViewById<EditText>(R.id.birthOfDate)

        // Set up DatePickerDialog for birth date
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    etDate.setText(String.format("%02d/%02d/%d", day, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = Calendar.getInstance().apply { set(2025, 3, 5) }.timeInMillis
                show()
            }
        }

        // Format manual input to DD/MM/YYYY
        etDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().filter { it.isDigit() }
                val formatted = StringBuilder()
                for (i in input.indices) {
                    if (i == 2 || i == 4) formatted.append('/')
                    if (i < 8) formatted.append(input[i])
                }
                etDate.removeTextChangedListener(this)
                etDate.setText(formatted)
                etDate.setSelection(formatted.length)
                etDate.addTextChangedListener(this)
            }
        })

        // Navigate to verifyFragment on button click
        btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_verifyFragment)
        }
    }
}