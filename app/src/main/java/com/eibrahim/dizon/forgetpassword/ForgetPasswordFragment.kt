package com.eibrahim.dizon.forgetpassword

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
import com.google.android.material.button.MaterialButton

class ForgetPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ForgetPasswordFragment()
    }

    private val viewModel: ForgetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnSendResetLink = view.findViewById<MaterialButton>(R.id.btnSendResetLink)

        btnSendResetLink.setOnClickListener {
            val email = etEmail.text.toString()
            viewModel.sendPasswordReset(email)
        }

        viewModel.resetPasswordState.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(requireContext(), "Reset link sent successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_forgetPasswordFragment_to_resetPasswordFragment)
                            },
                onFailure = { error ->
                    Toast.makeText(requireContext(), error.message ?: "Failed to send reset link", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}