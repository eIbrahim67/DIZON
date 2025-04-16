package com.eibrahim.dizon.deleteaccount.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eibrahim.dizon.R
import com.eibrahim.dizon.databinding.FragmentDeleteAccountBinding
import com.eibrahim.dizon.deleteaccount.viewmodel.DeleteAccountViewModel
import com.google.android.material.button.MaterialButton

class DeleteAccountFragment : Fragment() {

    private var _binding: FragmentDeleteAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeleteAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backArrow.setOnClickListener {
            viewModel.onGoBackClicked()
        }

        binding.btnGoBack.setOnClickListener {
            viewModel.onGoBackClicked()
        }

        binding.btnDeleteAccount.setOnClickListener {
            viewModel.onDeleteAccountClicked()
        }

        viewModel.showConfirmationDialog.observe(viewLifecycleOwner) { showDialog ->
            if (showDialog == true) {
                showConfirmationDialog()
            }
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate == true) {
                requireActivity().onBackPressed()
            }
        }

        viewModel.deleteCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed == true) {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun showConfirmationDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirm_delete)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        val btnConfirm = dialog.findViewById<MaterialButton>(R.id.btnConfirm)
        val loadingIndicator = dialog.findViewById<View>(R.id.loadingIndicator)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading == true) {
                btnCancel.visibility = View.GONE
                btnConfirm.visibility = View.GONE
                loadingIndicator.visibility = View.VISIBLE
            }
        }

        btnCancel.setOnClickListener {
            viewModel.onCancelDelete()
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            viewModel.onConfirmDelete()
        }

        dialog.setOnDismissListener {
            viewModel.onCancelDelete()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}