package com.eibrahim.dizon.change_password

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.eibrahim.dizon.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ChangePasswordFragment()
    }
    private lateinit var bottomNavigationView: BottomNavigationView
    // Lazily initialize the ViewModel
    private val viewModel: ChangePasswordViewModel by viewModels()

    // UI components to be initialized in onCreateView
    private lateinit var currentPasswordEditText: TextInputEditText
    private lateinit var newPasswordEditText: TextInputEditText
    private lateinit var confirmNewPasswordEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)

        // Hide bottom navigation
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        // Initialize UI components
        currentPasswordEditText = view.findViewById(R.id.currentPasswordEditText)
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText)
        confirmNewPasswordEditText = view.findViewById(R.id.confirmNewPasswordEditText)
        val resetButton = view.findViewById<MaterialButton>(R.id.resetButton)
        val backButton = view.findViewById<ImageView>(R.id.backButton)

        // Set up click listener for the back button
        backButton.setOnClickListener {
            // Navigate back when the back button is clicked
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        // Set up click listener for the reset button
        resetButton.setOnClickListener {
            val oldPassword = currentPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmNewPassword = confirmNewPasswordEditText.text.toString().trim()

            // Validate that all fields are filled
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Snackbar.make(view, "All fields are required", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate that new password matches confirmation
            if (newPassword != confirmNewPassword) {
                Snackbar.make(view, "New passwords do not match", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call the ViewModel to change the password
            viewModel.changePassword(oldPassword, newPassword, confirmNewPassword)
        }

        // Observe the ViewModel's state and update the UI accordingly
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.changePasswordState.collect { state ->
                when (state) {
                    is ChangePasswordState.Loading -> {
                        // Disable button to prevent multiple clicks during loading
                        resetButton.isEnabled = false
                    }
                    is ChangePasswordState.Success -> {
                        // Re-enable button and show success message
                        resetButton.isEnabled = true
                        Snackbar.make(view, state.message, Snackbar.LENGTH_SHORT).show()
                        // Clear fields after success (optional)
                        currentPasswordEditText.text?.clear()
                        newPasswordEditText.text?.clear()
                        confirmNewPasswordEditText.text?.clear()
                    }
                    is ChangePasswordState.Error -> {
                        // Re-enable button and show error message
                        resetButton.isEnabled = true
                        Snackbar.make(view, state.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is ChangePasswordState.Initial -> {
                        // No action needed for initial state
                    }
                }
            }
        }

        return view
    }


}