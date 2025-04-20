package com.eibrahim.dizon.auth.resetpassword.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.resetpassword.viewmodel.ResetPasswordRequest
import com.eibrahim.dizon.auth.resetpassword.viewmodel.ResetPasswordState
import com.eibrahim.dizon.auth.resetpassword.viewmodel.ResetPasswordViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ResetPasswordFragment : Fragment() {

    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ResetPasswordFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_reset_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ResetPasswordFragment", "onViewCreated called")

        val newPasswordTextInputLayout = view.findViewById<TextInputLayout>(R.id.newPasswordTextInputLayout)
        val confirmPasswordTextInputLayout = view.findViewById<TextInputLayout>(R.id.confirmPasswordTextInputLayout)
        val newPasswordEditText = view.findViewById<TextInputEditText>(R.id.newPasswordTextInputEditText)
        val confirmPasswordEditText = view.findViewById<TextInputEditText>(R.id.confirmPasswordTextInputEditText)
        val btnResetPassword = view.findViewById<MaterialButton>(R.id.btnResetPassword)
        val errorTextView = view.findViewById<TextView>(R.id.errorTextView)

        // Get email from arguments
        val email = arguments?.getString("email") ?: run {
            Log.e("ResetPasswordFragment", "Email not provided in arguments")
            Toast.makeText(requireContext(), R.string.error_email_not_provided, Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_resetPasswordFragment_to_forgetPasswordFragment)
            return
        }
        Log.d("ResetPasswordFragment", "Received email: $email")

        // Observe ViewModel state
        viewModel.resetPasswordState.observe(viewLifecycleOwner) { state ->
            Log.d("ResetPasswordFragment", "ResetPasswordState changed: $state")
            when (state) {
                is ResetPasswordState.Loading -> {
                    btnResetPassword.isEnabled = false
                    Toast.makeText(requireContext(), R.string.reset_password_loading, Toast.LENGTH_SHORT).show()
                }
                is ResetPasswordState.Success -> {
                    btnResetPassword.isEnabled = true
                    Log.d("ResetPasswordFragment", "Success response: ${state.response}")
                    Toast.makeText(requireContext(), R.string.reset_password_success, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFragment)
                }
                is ResetPasswordState.Error -> {
                    btnResetPassword.isEnabled = true
                    Log.e("ResetPasswordFragment", "Error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Reset Password button click
        btnResetPassword.setOnClickListener {
            Log.d("ResetPasswordFragment", "Reset Password button clicked")
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Clear previous errors
            newPasswordTextInputLayout.error = null
            confirmPasswordTextInputLayout.error = null
            errorTextView.visibility = View.GONE

            // Validate inputs
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                newPasswordTextInputLayout.error = getString(R.string.error_empty_password)
                confirmPasswordTextInputLayout.error = getString(R.string.error_empty_password)
                Log.d("ResetPasswordFragment", "Empty password fields")
                return@setOnClickListener
            }

            // Validate password length (at least 8 characters)
            if (newPassword.length < 8) {
                newPasswordTextInputLayout.error = getString(R.string.error_password_too_short)
                Log.d("ResetPasswordFragment", "Password too short: $newPassword")
                return@setOnClickListener
            }

            // Validate password complexity (letters, numbers, special characters)
            val passwordPattern = Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
            if (!newPassword.matches(passwordPattern)) {
                newPasswordTextInputLayout.error = getString(R.string.error_password_complexity)
                Log.d("ResetPasswordFragment", "Password does not meet complexity requirements: $newPassword")
                return@setOnClickListener
            }

            // Validate password match
            if (newPassword != confirmPassword) {
                confirmPasswordTextInputLayout.error = getString(R.string.error_passwords_not_match)
                Log.d("ResetPasswordFragment", "Passwords do not match")
                return@setOnClickListener
            }

            // Proceed with reset password request
            val request = ResetPasswordRequest(
                email = email,
                newPassword = newPassword,
                confirmPassword = confirmPassword
            )
            Log.d("ResetPasswordFragment", "Calling resetPassword with request: $request")
            viewModel.resetPassword(request)
        }
    }
}