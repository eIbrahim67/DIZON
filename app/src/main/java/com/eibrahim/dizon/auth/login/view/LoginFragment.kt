package com.eibrahim.dizon.auth.login.view

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.login.viewModel.LoginState
import com.eibrahim.dizon.auth.login.viewModel.LoginViewModel
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.emailLogin)
        val etPassword = view.findViewById<EditText>(R.id.passwordLogin)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val emailInputLayout = view.findViewById<TextInputLayout>(R.id.emailTextInputLayout)
        val passwordInputLayout = view.findViewById<TextInputLayout>(R.id.passwordTextInputLayout)

        view.findViewById<TextView>(R.id.txtForgotPassword).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgetPasswordFragment)
        }

        view.findViewById<TextView>(R.id.txtRegister).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            emailInputLayout.error = null
            passwordInputLayout.error = null

            var isValid = true

            if (email.isEmpty()) {
                emailInputLayout.error = getString(R.string.error_email_required)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInputLayout.error = getString(R.string.error_invalid_email)
                isValid = false
            }

            if (password.isEmpty()) {
                passwordInputLayout.error = getString(R.string.error_empty_password)
                isValid = false
            }

            if (isValid) {
                viewModel.login(email, password)
            }
        }

        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginState.Loading -> {
                    btnLogin.isEnabled = false
                    Toast.makeText(requireContext(), "Logging in...", Toast.LENGTH_SHORT).show()
                }
                is LoginState.Success -> {
                    btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
                is LoginState.Error -> {
                    btnLogin.isEnabled = true
                    Log.d("LoginFragment", "Error message: ${state.message}")

                    // reset old errors
                    emailInputLayout.error = null
                    passwordInputLayout.error = null

                    if (state.message.contains("Invalid email or password", ignoreCase = true)) {
                        passwordInputLayout.error = state.message
                    } else {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
}