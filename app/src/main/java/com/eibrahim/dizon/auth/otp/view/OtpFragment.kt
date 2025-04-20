package com.eibrahim.dizon.auth.otp.view

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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.otp.viewModel.OtpRequest
import com.eibrahim.dizon.auth.otp.viewModel.OtpState
import com.eibrahim.dizon.auth.otp.viewModel.ResendOtpRequest
import com.eibrahim.dizon.auth.otp.viewModel.ResendOtpState
import com.eibrahim.dizon.auth.otp.viewModel.OtpViewModel

class OtpFragment : Fragment() {

    private val viewModel: OtpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("OtpFragment", "onCreateView called")
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("OtpFragment", "onViewCreated called")

        val btnResetPassword = view.findViewById<Button>(R.id.btn_reset_password)
        val tvResendOtp = view.findViewById<TextView>(R.id.textView13)
        val otpDigit1 = view.findViewById<EditText>(R.id.otp_digit_1)
        val otpDigit2 = view.findViewById<EditText>(R.id.otp_digit_2)
        val otpDigit3 = view.findViewById<EditText>(R.id.otp_digit_3)
        val otpDigit4 = view.findViewById<EditText>(R.id.otp_digit_4)
        val otpDigit5 = view.findViewById<EditText>(R.id.otp_digit_5)
        val otpDigit6 = view.findViewById<EditText>(R.id.otp_digit_6)

        // Get email from arguments
        val email = arguments?.getString("email") ?: run {
            Log.e("OtpFragment", "Email not provided in arguments")
            Toast.makeText(requireContext(), R.string.error_email_not_provided, Toast.LENGTH_LONG).show()
            try {
                findNavController().navigate(R.id.action_otpFragment_to_forgetPasswordFragment)
            } catch (e: Exception) {
                Log.e("OtpFragment", "Navigation error: ${e.message}", e)
                findNavController().popBackStack()
            }
            return
        }
        Log.d("OtpFragment", "Received email: $email")

        // Handle back press
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("OtpFragment", "Back button pressed, navigating to ForgetPasswordFragment")
                try {
                    val bundle = Bundle().apply {
                        putString("email", email)
                    }
                    findNavController().navigate(R.id.action_otpFragment_to_forgetPasswordFragment, bundle)
                } catch (e: Exception) {
                    Log.e("OtpFragment", "Back navigation error: ${e.message}", e)
                    findNavController().popBackStack(R.id.forgetPasswordFragment, false)
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Auto-move to next OTP field
        val otpFields = listOf(otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6)
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

        // Observe OTP verification state
        viewModel.otpState.observe(viewLifecycleOwner) { state ->
            Log.d("OtpFragment", "OtpState changed: $state")
            when (state) {
                is OtpState.Loading -> {
                    btnResetPassword.isEnabled = false
                    Toast.makeText(requireContext(), R.string.otp_verifying, Toast.LENGTH_SHORT).show()
                }
                is OtpState.Success -> {
                    btnResetPassword.isEnabled = true
                    Log.d("OtpFragment", "Success response: ${state.response}")
                    Toast.makeText(requireContext(), R.string.otp_success, Toast.LENGTH_SHORT).show()
                    val bundle = Bundle().apply {
                        putString("email", email)
                    }
                    Log.d("OtpFragment", "Navigating to ResetPasswordFragment with email: $email")
                    try {
                        findNavController().navigate(R.id.action_otpFragment_to_resetPasswordFragment, bundle)
                    } catch (e: Exception) {
                        Log.e("OtpFragment", "Navigation error: ${e.message}", e)
                        Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show()
                    }
                }
                is OtpState.Error -> {
                    btnResetPassword.isEnabled = true
                    Log.e("OtpFragment", "Error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Observe Resend OTP state
        viewModel.resendOtpState.observe(viewLifecycleOwner) { state ->
            Log.d("OtpFragment", "ResendOtpState changed: $state")
            when (state) {
                is ResendOtpState.Loading -> {
                    tvResendOtp.isEnabled = false
                    Toast.makeText(requireContext(), R.string.resend_otp_loading, Toast.LENGTH_SHORT).show()
                }
                is ResendOtpState.Success -> {
                    tvResendOtp.isEnabled = true
                    Log.d("OtpFragment", "Resend success: ${state.response}")
                    Toast.makeText(requireContext(), R.string.resend_otp_success, Toast.LENGTH_SHORT).show()
                }
                is ResendOtpState.Error -> {
                    tvResendOtp.isEnabled = true
                    Log.e("OtpFragment", "Resend error: ${state.message}")
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Verify OTP button click
        btnResetPassword.setOnClickListener {
            Log.d("OtpFragment", "Reset Password button clicked")
            val otp = otpFields.joinToString("") { it.text.toString().trim() }
            if (otp.length != 6 || !otp.matches(Regex("\\d{6}"))) {
                Toast.makeText(requireContext(), R.string.error_invalid_otp, Toast.LENGTH_SHORT).show()
                Log.d("OtpFragment", "Invalid OTP: $otp")
                return@setOnClickListener
            }

            val request = OtpRequest(
                email = email,
                otp = otp
            )
            Log.d("OtpFragment", "Calling verifyOtp with request: $request")
            viewModel.verifyOtp(request)
        }

        // Resend OTP click
        tvResendOtp.setOnClickListener {
            Log.d("OtpFragment", "Resend OTP clicked")
            val request = ResendOtpRequest(email = email)
            Log.d("OtpFragment", "Calling resendOtp with request: $request")
            viewModel.resendOtp(request)
        }
    }
}