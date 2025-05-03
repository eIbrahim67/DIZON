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
import com.eibrahim.dizon.auth.AuthActivity
import com.eibrahim.dizon.auth.AuthPreferences
import com.eibrahim.dizon.auth.login.viewModel.LoginState
import com.eibrahim.dizon.auth.login.viewModel.LoginViewModel
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var authPreferences: AuthPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LoginFragment", "onViewCreated called")

        // Initialize AuthPreferences
        authPreferences = AuthPreferences(requireContext())

        val etEmail = view.findViewById<EditText>(R.id.emailLogin)
        val etPassword = view.findViewById<EditText>(R.id.passwordLogin)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnSignInWithGoogle = view.findViewById<Button>(R.id.signinWithGoogle)
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
                viewModel.login(email, password, authPreferences)
            }
        }

        // Handle Google Sign-In button click
        btnSignInWithGoogle.setOnClickListener {
            Log.d("LoginFragment", "Google Sign-In button clicked")
            Toast.makeText(requireContext(), "Starting Google Sign-In...", Toast.LENGTH_SHORT).show()
            val signInIntent = (activity as AuthActivity).googleSignInClient.signInIntent
            startActivityForResult(signInIntent, AuthActivity.RC_SIGN_IN)
        }

        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            Log.d("LoginFragment", "LoginState changed: $state")
            when (state) {
                is LoginState.Loading -> {
                    btnLogin.isEnabled = false
                    btnSignInWithGoogle.isEnabled = false
                }
                is LoginState.Success -> {
                    btnLogin.isEnabled = true
                    btnSignInWithGoogle.isEnabled = true
                    Log.d("LoginFragment", "Success response: ${state.response}")
                    // Log the saved token
                    val savedToken = authPreferences.getToken()
                    Log.d("LoginFragment", "Saved token: $savedToken")
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    view.postDelayed({
                        requireActivity().finish()
                    }, 1000)
                }
                is LoginState.Error -> {
                    btnLogin.isEnabled = true
                    btnSignInWithGoogle.isEnabled = true
                    Log.e("LoginFragment", "Error message: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Handle Google Sign-In result from arguments
        arguments?.let { args ->
            Log.d("LoginFragment", "Arguments received: $args")
            args.getString("googleIdToken")?.let { idToken ->
                Log.d("LoginFragment", "Received Google idToken: $idToken")
                Toast.makeText(requireContext(), "Processing Google Sign-In...", Toast.LENGTH_SHORT).show()
                viewModel.loginWithGoogle(idToken, authPreferences)
            }
            args.getString("error")?.let { error ->
                Log.e("LoginFragment", "Google Sign-In error: $error")
                Toast.makeText(requireContext(), "Google Sign-In failed: $error", Toast.LENGTH_LONG).show()
            }
            arguments = null // Clear arguments after handling
        }
    }
}