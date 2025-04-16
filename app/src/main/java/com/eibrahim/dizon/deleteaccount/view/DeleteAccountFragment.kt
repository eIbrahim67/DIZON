package com.eibrahim.dizon.deleteaccount.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.eibrahim.dizon.R
import com.eibrahim.dizon.deleteaccount.viewmodel.DeleteAccountViewModel
import com.google.android.material.button.MaterialButton

class DeleteAccountFragment : Fragment() {

    private val viewModel: DeleteAccountViewModel by viewModels()
    private var confirmationDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_delete_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.backArrow).setOnClickListener {
            viewModel.onGoBackClicked()
        }

        view.findViewById<MaterialButton>(R.id.btnGoBack).setOnClickListener {
            viewModel.onGoBackClicked()
        }

        view.findViewById<MaterialButton>(R.id.btnDeleteAccount).setOnClickListener {
            viewModel.onDeleteAccountClicked()
        }

        viewModel.showConfirmationDialog.observe(viewLifecycleOwner) { showDialog ->
            if (showDialog == true) {
                showConfirmationDialog()
            }
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate == true) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        viewModel.deleteCompleted.observe(viewLifecycleOwner) { completed ->
            if (completed == true) {
                confirmationDialog?.dismiss()
                confirmationDialog = null
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showConfirmationDialog() {
        confirmationDialog?.dismiss() // Dismiss any existing dialog
        confirmationDialog = Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_confirm_delete)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val btnCancel = findViewById<MaterialButton>(R.id.btnCancel)
            val btnConfirm = findViewById<MaterialButton>(R.id.btnConfirm)
            val loadingIndicator = findViewById<View>(R.id.loadingIndicator)

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading == true) {
                    btnCancel.visibility = View.GONE
                    btnConfirm.visibility = View.GONE
                    loadingIndicator.visibility = View.VISIBLE
                } else {
                    btnCancel.visibility = View.VISIBLE
                    btnConfirm.visibility = View.VISIBLE
                    loadingIndicator.visibility = View.GONE
                }
            }

            btnCancel.setOnClickListener {
                viewModel.onCancelDelete()
                dismiss()
            }

            btnConfirm.setOnClickListener {
                viewModel.onConfirmDelete()
            }

            setOnDismissListener {
                viewModel.onCancelDelete()
                confirmationDialog = null
            }

            show()
        }
    }

    override fun onDestroyView() {
        confirmationDialog?.dismiss()
        confirmationDialog = null
        super.onDestroyView()
    }
}