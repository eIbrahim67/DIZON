package com.eibrahim.dizon.register.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.register.viewModel.RegisterViewModel

typealias RegistrationResult = Result<Boolean>

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()


    companion object {
        fun newInstance() = RegisterFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val etName = view.findViewById<EditText>(R.id.nameEditText)
        val etEmail = view.findViewById<EditText>(R.id.emailEditText)
        val etPhone = view.findViewById<EditText>(R.id.numberEditText)
        val etCity = view.findViewById<EditText>(R.id.cityEditText)
        val etPassword = view.findViewById<EditText>(R.id.passwordRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val phone = etPhone.text.toString()
            val city = etCity.text.toString()
            val password = etPassword.text.toString()

            viewModel.registerUser(name, email, phone, city, password)
        }
        viewModel.registrationState.observe(viewLifecycleOwner) { result ->
            handleRegistrationResult(result)
        }
    }

    private fun handleRegistrationResult(result: RegistrationResult) {
        result.fold(
            onSuccess = {
                Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_verifyFragment)
            },
            onFailure = { error ->
                Toast.makeText(requireContext(), error.message ?: "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        )
    }
}