package com.eibrahim.dizon.auth.forgetpassword.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.forgetpassword.viewModel.ForgetPasswordRequest
import com.eibrahim.dizon.auth.forgetpassword.viewModel.ForgetPasswordState
import com.eibrahim.dizon.auth.forgetpassword.viewModel.ForgetPasswordViewModel
import com.google.android.material.button.MaterialButton

class ForgetPasswordFragment : Fragment() {

    private val viewModel: ForgetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("ForgetPasswordFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("ForgetPasswordFragment", "onViewCreated called")
        Log.d("ForgetPasswordFragment", "Current forgetPasswordState: ${viewModel.forgetPasswordState.value}")

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnSendResetLink = view.findViewById<MaterialButton>(R.id.btnSendResetLink)

        // Observe ViewModel state
        viewModel.forgetPasswordState.observe(viewLifecycleOwner) { state ->
            Log.d("ForgetPasswordFragment", "ForgetPasswordState changed: $state")
            when (state) {
                is ForgetPasswordState.Idle -> {
                    btnSendResetLink.isEnabled = true
                }
                is ForgetPasswordState.Loading -> {
                    btnSendResetLink.isEnabled = false
                    Toast.makeText(requireContext(), R.string.forget_password_loading, Toast.LENGTH_SHORT).show()
                }
                is ForgetPasswordState.Success -> {
                    btnSendResetLink.isEnabled = true
                    Log.d("ForgetPasswordFragment", "Success response: ${state.response}")
                    Toast.makeText(requireContext(), R.string.forget_password_success, Toast.LENGTH_SHORT).show()
                    val email = etEmail.text.toString().trim()
                    val bundle = Bundle().apply {
                        putString("email", email)
                    }
                    Log.d("ForgetPasswordFragment", "Navigating to OtpFragment with email: $email")
                    try {
                        findNavController().navigate(R.id.action_forgetPasswordFragment_to_otpFragment, bundle)
                        viewModel.resetState() // Reset state after navigation
                    } catch (e: Exception) {
                        Log.e("ForgetPasswordFragment", "Navigation error: ${e.message}", e)
                        Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show()
                    }
                }
                is ForgetPasswordState.Error -> {
                    btnSendResetLink.isEnabled = true
                    Log.e("ForgetPasswordFragment", "Error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Send reset link button click
        btnSendResetLink.setOnClickListener {
            Log.d("ForgetPasswordFragment", "Send Reset Link button clicked")
            val email = etEmail.text.toString().trim()
            if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                Toast.makeText(requireContext(), R.string.error_invalid_email, Toast.LENGTH_SHORT).show()
                Log.d("ForgetPasswordFragment", "Invalid email: $email")
                return@setOnClickListener
            }

            val request = ForgetPasswordRequest(email = email)
            Log.d("ForgetPasswordViewModel", "Calling sendResetLink with request: $request")
            viewModel.sendResetLink(request)
        }
    }
}