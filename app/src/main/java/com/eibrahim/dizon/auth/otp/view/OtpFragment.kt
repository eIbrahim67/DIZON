package com.eibrahim.dizon.auth.otp.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.otp.viewModel.OtpViewModel

class OtpFragment : Fragment() {

    companion object {
        fun newInstance() = OtpFragment()
    }

    private val viewModel: OtpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }
}