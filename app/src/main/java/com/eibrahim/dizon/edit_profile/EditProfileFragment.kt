package com.eibrahim.dizon.edit_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File

class EditProfileFragment : Fragment() {

    companion object {
        fun newInstance() = EditProfileFragment()
    }

    private lateinit var bottomNavigationView: BottomNavigationView

    private val viewModel: EditProfileViewModel by viewModels()
    private var selectedImageFile: File? = null

    override fun onResume() {
        super.onResume()
        bottomNavigationView.visibility = View.GONE
    }

    // Launcher for picking an image
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                // Properly convert URI to File using ContentResolver
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val file = File(requireContext().cacheDir, "new_profile_image_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                selectedImageFile = file
                Toast.makeText(context, "Image selected successfully", Toast.LENGTH_SHORT).show()

                // Load the selected image into img_profile
                val imgProfile = view?.findViewById<ImageView>(R.id.img_profile)
                imgProfile?.let {
                    Glide.with(this)
                        .load(selectedImageFile)
                        .circleCrop()
                        .into(it)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to select image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure RetrofitClient is initialized with AuthPreferences
        RetrofitClient.initAuthPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        // Initialize UI components
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        val firstNameInput = view.findViewById<TextInputEditText>(R.id.FirstNameInput)
        val lastNameInput = view.findViewById<TextInputEditText>(R.id.LastNameInput)
        val emailInput = view.findViewById<TextInputEditText>(R.id.EmailInput)
        val phoneInput = view.findViewById<TextInputEditText>(R.id.phoneInput)
        val updateButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.UpdateButton)
        val imgEditOverlay = view.findViewById<ImageView>(R.id.edit_icon)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val cityAutoComplete = view.findViewById<AutoCompleteTextView>(R.id.cityAutoComplete)
        val imgProfile = view.findViewById<ImageView>(R.id.img_profile)
        val cityInputLayout = view.findViewById<TextInputLayout>(R.id.cityInputLayout)

        // Set up back button click listener
        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        val cities = listOf(
            "Cairo", "Giza", "Alexandria", "Dakahlia", "Red Sea", "Beheira", "Fayoum", "Gharbia", "Ismailia", "Menofia", "Minya",
            "Qaliubiya", "New Valley", "Suez", "Aswan", "Assiut", "Beni Suef", "Port Said", "Damietta", "Sharkia", "South Sinai",
            "Kafr El Sheikh", "Matrouh", "Luxor", "Qena", "North Sinai", "Sohag", "Helwan", "6th of October"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        cityAutoComplete.setAdapter(adapter)
        cityAutoComplete.threshold = 1

        cityAutoComplete.setOnClickListener {
            cityAutoComplete.showDropDown()
        }

        cityInputLayout.setEndIconOnClickListener {
            cityAutoComplete.showDropDown()
        }

        // Observe ViewModel data
        viewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                firstNameInput.setText(it.firstName ?: "")
                lastNameInput.setText(it.lastName ?: "")
                emailInput.setText(it.email ?: "")
                phoneInput.setText(it.phoneNumber ?: "")
                cityAutoComplete.setText(it.city ?: "", false)

                // Concatenate first and last name for tv_name
                val fullName = "${it.firstName ?: ""} ${it.lastName ?: ""}".trim()
                tvName.text = if (fullName.isNotEmpty()) fullName else getString(R.string.user_name)

                // Load imageUrl into img_profile if available, otherwise use default
                if (!it.imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(it.imageUrl)
                        .circleCrop()
                        .into(imgProfile)
                } else {
                    imgProfile.setImageResource(R.drawable.man)
                }
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            updateButton.isEnabled = !isLoading
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.successMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                // Clear the message after displaying to prevent re-displaying on configuration changes
                viewModel._successMessage.value = null
                // Reset selectedImageFile after successful update
                selectedImageFile = null
            }
        })

        // Fetch initial user data
        viewModel.fetchUserData()

        // Set up image selection
        imgEditOverlay.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        imgProfile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Set up update button
        updateButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val city = cityAutoComplete.text.toString().trim()

            // Basic validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || city.isEmpty()) {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pass the context's cache directory to the viewModel
            viewModel.updateUserData(
                firstName,
                lastName,
                email,
                phone,
                city,
                selectedImageFile,
                requireContext().cacheDir
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomNavigationView.visibility = View.VISIBLE

        // Clean up selected image file if it exists
        selectedImageFile?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
    }
}