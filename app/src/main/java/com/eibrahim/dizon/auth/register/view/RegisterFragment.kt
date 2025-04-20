package com.eibrahim.dizon.auth.register.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.register.viewModel.RegisterRequest
import com.eibrahim.dizon.auth.register.viewModel.RegisterState
import com.eibrahim.dizon.auth.register.viewModel.RegisterViewModel
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RegisterFragment", "onViewCreated called")

        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val etFirstName = view.findViewById<EditText>(R.id.firstNameEditText)
        val etLastName = view.findViewById<EditText>(R.id.lastNameEditText)
        val etEmail = view.findViewById<EditText>(R.id.emailRegister)
        val etPhone = view.findViewById<EditText>(R.id.numberEditText)
        val etCity = view.findViewById<EditText>(R.id.cityEditText)
        val etDate = view.findViewById<EditText>(R.id.birthOfDate)
        val etPassword = view.findViewById<EditText>(R.id.passwordRegister)
        val etConfirmPassword = view.findViewById<EditText>(R.id.confirmPassword)
        val cbTerms = view.findViewById<CheckBox>(R.id.checkBox)
        val phoneInputLayout = view.findViewById<TextInputLayout>(R.id.phoneInputLayout)
        val cityInputLayout = view.findViewById<TextInputLayout>(R.id.cityInputLayout)

        // Set up Spinner for city selection
        val spinnerCity = view.findViewById<Spinner>(R.id.spinnerCity2)
        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCity = parent?.getItemAtPosition(position).toString()
                Log.d("RegisterFragment", "City selected: $selectedCity")
                etCity.setText(selectedCity)
                // Update hint based on city input
                cityInputLayout.hint = if (selectedCity.isNotEmpty()) "" else getString(R.string.ex_city)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                etCity.setText("")
                cityInputLayout.hint = getString(R.string.ex_city)
            }
        }

        // Set up TextWatcher for numberEditText to control hint
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                phoneInputLayout.hint = if (s.isNullOrEmpty()) getString(R.string.ex_phone) else ""
            }
        })

        // Set up TextWatcher for cityEditText to control hint
        etCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                cityInputLayout.hint = if (s.isNullOrEmpty()) getString(R.string.ex_city) else ""
            }
        })

        // Set up DatePickerDialog for birth date
        etDate.setOnClickListener {
            Log.d("RegisterFragment", "Date picker clicked")
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

        // Observe ViewModel state
        Log.d("RegisterFragment", "Setting up registerState observer")
        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            Log.d("RegisterFragment", "RegisterState changed: $state")
            when (state) {
                is RegisterState.Loading -> {
                    btnRegister.isEnabled = false
                    Toast.makeText(requireContext(), R.string.register_loading, Toast.LENGTH_SHORT).show()
                }
                is RegisterState.Success -> {
                    btnRegister.isEnabled = true
                    Log.d("RegisterFragment", "Success response: ${state.response}")
                    Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_SHORT).show()
                    view.postDelayed({
                        val bundle = Bundle().apply {
                            putString("email", etEmail.text.toString().trim())
                        }
                        findNavController().navigate(R.id.action_registerFragment_to_verifyFragment, bundle)
                    }, 1000) // Delay navigation by 1 second
                }
                is RegisterState.Error -> {
                    btnRegister.isEnabled = true
                    Log.e("RegisterFragment", "Error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Register button click
        btnRegister.setOnClickListener {
            Log.d("RegisterFragment", "Register button clicked")
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val city = etCity.text.toString().trim()
            val birthDate = etDate.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val isTermsAccepted = cbTerms.isChecked

            if (validateInputs(firstName, lastName, email, phone, city, birthDate, password, confirmPassword, isTermsAccepted)) {
                Log.d("RegisterFragment", "Inputs valid, creating RegisterRequest")
                val request = RegisterRequest(
                    firstName = firstName,
                    lastName = lastName.ifBlank { null },
                    email = email,
                    phoneNumber = phone.ifBlank { null },
                    city = city.ifBlank { null },
                    birthOfDate = birthDate.ifBlank { null },
                    password = password,
                    confirmPassword = confirmPassword,
                    isTermsAccepted = isTermsAccepted
                )
                Log.d("RegisterFragment", "Calling registerUser with request: $request")
                viewModel.registerUser(request)
            } else {
                Log.d("RegisterFragment", "Input validation failed")
            }
        }
    }

    private fun validateInputs(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        city: String,
        birthDate: String,
        password: String,
        confirmPassword: String,
        isTermsAccepted: Boolean
    ): Boolean {
        Log.d("RegisterFragment", "Validating inputs")
        // Reset errors
        view?.findViewById<TextInputLayout>(R.id.firstNameInputLayout)?.error = null
        view?.findViewById<TextInputLayout>(R.id.lastNameInputLayout)?.error = null
        view?.findViewById<TextInputLayout>(R.id.emailInputLayout)?.error = null
        view?.findViewById<TextInputLayout>(R.id.phoneInputLayout)?.error = null
        view?.findViewById<TextInputLayout>(R.id.cityInputLayout)?.error = null
        view?.findViewById<TextInputLayout>(R.id.birthDateInputLayout)?.error = null
        view?.findViewById<TextInputLayout>(R.id.passwordInputLayout)?.error = null
        view?.findViewById<TextInputLayout>(R.id.confirmPasswordInputLayout)?.error = null

        var isValid = true

        if (firstName.isBlank()) {
            val layout = view?.findViewById<TextInputLayout>(R.id.firstNameInputLayout)
            layout?.error = getString(R.string.error_first_name_required)
            Log.d("RegisterFragment", "First name error set: ${layout?.error}")
            isValid = false
        }

        // Last name is optional, no validation needed

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val layout = view?.findViewById<TextInputLayout>(R.id.emailInputLayout)
            layout?.error = getString(R.string.error_invalid_email)
            Log.d("RegisterFragment", "Email error set: ${layout?.error}")
            isValid = false
        }

        if (phone.isNotBlank() && (phone.length < 10 || !phone.matches(Regex("\\d+")))) {
            val layout = view?.findViewById<TextInputLayout>(R.id.phoneInputLayout)
            layout?.error = getString(R.string.error_invalid_phone)
            Log.d("RegisterFragment", "Phone error set: ${layout?.error}")
            isValid = false
        }

        // City is optional, no validation needed

        if (birthDate.isNotBlank() && !birthDate.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
            val layout = view?.findViewById<TextInputLayout>(R.id.birthDateInputLayout)
            layout?.error = getString(R.string.error_invalid_date)
            Log.d("RegisterFragment", "Birth date error set: ${layout?.error}")
            isValid = false
        }

        // Validate password length and complexity
        if (password.isEmpty()) {
            val layout = view?.findViewById<TextInputLayout>(R.id.passwordInputLayout)
            layout?.error = getString(R.string.error_empty_password)
            Log.d("RegisterFragment", "Password empty error set: ${layout?.error}")
            isValid = false
        } else if (password.length < 8) {
            val layout = view?.findViewById<TextInputLayout>(R.id.passwordInputLayout)
            layout?.error = getString(R.string.error_password_short)
            Log.d("RegisterFragment", "Password error set: ${layout?.error}")
            isValid = false
        } else {
            val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
            if (!password.matches(passwordPattern)) {
                val layout = view?.findViewById<TextInputLayout>(R.id.passwordInputLayout)
                layout?.error = getString(R.string.error_password_complexity)
                Log.d("RegisterFragment", "Password complexity error set: ${layout?.error}")
                isValid = false
            }
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            val layout = view?.findViewById<TextInputLayout>(R.id.confirmPasswordInputLayout)
            layout?.error = getString(R.string.error_empty_password)
            Log.d("RegisterFragment", "Confirm password empty error set: ${layout?.error}")
            isValid = false
        } else if (password != confirmPassword) {
            val layout = view?.findViewById<TextInputLayout>(R.id.confirmPasswordInputLayout)
            layout?.error = getString(R.string.error_password_mismatch)
            Log.d("RegisterFragment", "Confirm password error set: ${layout?.error}")
            isValid = false
        }

        if (!isTermsAccepted) {
            Toast.makeText(requireContext(), R.string.error_terms_not_accepted, Toast.LENGTH_SHORT).show()
            Log.d("RegisterFragment", "Terms not accepted")
            isValid = false
        }

        Log.d("RegisterFragment", "Validation result: $isValid")
        return isValid
    }
}