package com.eibrahim.dizon.auth.forgetpassword.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.forgetpassword.viewModel.ForgetPasswordViewModel
import com.google.android.material.button.MaterialButton

class ForgetPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ForgetPasswordFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_forget_password, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSendResetLink = view.findViewById<MaterialButton>(R.id.btnSendResetLink)

        btnSendResetLink.setOnClickListener {
            findNavController().navigate(R.id.action_forgetPasswordFragment_to_otpFragment)
        }
    }
}