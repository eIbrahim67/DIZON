package com.eibrahim.dizon.auth.verify.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.eibrahim.dizon.auth.verify.viewModel.VerifyRequest
import com.eibrahim.dizon.auth.verify.viewModel.VerifyState
import com.eibrahim.dizon.auth.verify.viewModel.VerifyViewModel

class VerifyFragment : Fragment() {

    private val viewModel: VerifyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("VerifyFragment", "onViewCreated called")

        val btnVerify = view.findViewById<Button>(R.id.btnVerify)
        val tvResendCode = view.findViewById<TextView>(R.id.tv_resend_code)
        val otpDigit1 = view.findViewById<EditText>(R.id.otp_digit_1)
        val otpDigit2 = view.findViewById<EditText>(R.id.otp_digit_2)
        val otpDigit3 = view.findViewById<EditText>(R.id.otp_digit_3)
        val otpDigit4 = view.findViewById<EditText>(R.id.otp_digit_4)
        val otpDigit5 = view.findViewById<EditText>(R.id.otp_digit_5)
        val otpDigit6 = view.findViewById<EditText>(R.id.otp_digit_6)


        // Get email from arguments
        val email = arguments?.getString("email") ?: run {
            Log.e("VerifyFragment", "Email not provided in arguments")
            Toast.makeText(requireContext(), "Error: Email not provided", Toast.LENGTH_LONG).show()
            return
        }
        Log.d("VerifyFragment", "Received email: $email")

        // Auto-move to next OTP field
        val otpFields = listOf(otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5,otpDigit6)
        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    } else if (s?.length == 0 && index > 0) {
                        otpFields[index - 1].requestFocus()
                    }
                }
            })
        }

        // Observe ViewModel state
        viewModel.verifyState.observe(viewLifecycleOwner) { state ->
            Log.d("VerifyFragment", "VerifyState changed: $state")
            when (state) {
                is VerifyState.Loading -> {
                    btnVerify.isEnabled = false
                    Toast.makeText(requireContext(), R.string.verify_loading, Toast.LENGTH_SHORT).show()
                }
                is VerifyState.Success -> {
                    btnVerify.isEnabled = true
                    Log.d("VerifyFragment", "Success response: ${state.response}")
                    Toast.makeText(requireContext(), R.string.verify_success, Toast.LENGTH_SHORT).show()
                    // Navigate to LoginFragment instead of finishing the activity
                    findNavController().navigate(R.id.loginFragment)
                }
                is VerifyState.Error -> {
                    btnVerify.isEnabled = true
                    Log.e("VerifyFragment", "Error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Verify button click
        btnVerify.setOnClickListener {
            Log.d("VerifyFragment", "Verify button clicked")
            val otp = otpFields.joinToString("") { it.text.toString().trim() }
            if (otp.length != 6) {
                Toast.makeText(requireContext(), R.string.error_invalid_otp, Toast.LENGTH_SHORT).show()
                Log.d("VerifyFragment", "Invalid OTP length: ${otp.length}")
                return@setOnClickListener
            }

            val request = VerifyRequest(
                email = email,
                otp = otp
            )
            Log.d("VerifyFragment", "Calling verifyOtp with request: $request")
            viewModel.verifyOtp(request)
        }

        // Resend code click
        tvResendCode.setOnClickListener {
            Log.d("VerifyFragment", "Resend code clicked")
            Toast.makeText(requireContext(), R.string.resend_code_sent, Toast.LENGTH_SHORT).show()
        }
    }
}