package com.eibrahim.dizon.verify.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.verify.viewModel.VerifyViewModel

class VerifyFragment : Fragment() {

    companion object {
        fun newInstance() = VerifyFragment()
    }

    private val viewModel: VerifyViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnVerify).setOnClickListener {
            requireActivity().finish()
        }
    }
}