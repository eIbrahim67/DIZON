package com.eibrahim.dizon.edit_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class EditProfileFragment : Fragment() {

    companion object {
        fun newInstance() = EditProfileFragment()
    }

    private val viewModel: EditProfileViewModel by viewModels()
    private var selectedImageFile: File? = null

    // Launcher for picking an image
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                // Properly convert URI to File using ContentResolver
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val file = File(requireContext().cacheDir, "profile_image.jpg")
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
                        .placeholder(R.drawable.man) // Default placeholder while loading
                        .error(R.drawable.man) // Fallback if loading fails
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

        // Initialize UI components
        val firstNameInput = view.findViewById<TextInputEditText>(R.id.FirstNameInput)
        val lastNameInput = view.findViewById<TextInputEditText>(R.id.LastNameInput)
        val emailInput = view.findViewById<TextInputEditText>(R.id.EmailInput)
        val phoneInput = view.findViewById<TextInputEditText>(R.id.phoneInput)
        val cityInput = view.findViewById<TextInputEditText>(R.id.CityInput)
        val updateButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.UpdateButton)
        val imgEditOverlay = view.findViewById<ImageView>(R.id.img_edit_overlay)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        val citySpinner = view.findViewById<Spinner>(R.id.spinnerCity2)
        val imgProfile = view.findViewById<ImageView>(R.id.img_profile)

        // Observe ViewModel data
        viewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            user?.let {
                firstNameInput.setText(it.firstName ?: "")
                lastNameInput.setText(it.lastName ?: "")
                emailInput.setText(it.email ?: "")
                phoneInput.setText(it.phoneNumber ?: "")
                cityInput.setText(it.city ?: "")
                // Concatenate first and last name for tv_name
                val fullName = "${it.firstName ?: ""} ${it.lastName ?: ""}".trim()
                tvName.text = if (fullName.isNotEmpty()) fullName else getString(R.string.user_name)

                // Load imageUrl into img_profile if available, otherwise use default
                if (!it.imageUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(it.imageUrl)
                        .placeholder(R.drawable.man)
                        .circleCrop() // Default placeholder while loading
                        .error(R.drawable.man) // Fallback if loading fails
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

        // Fetch initial user data
        viewModel.fetchUserData()

        // Set up image selection
        imgEditOverlay.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Set up Spinner to update CityInput
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCity = parent.getItemAtPosition(position).toString()
                cityInput.setText(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle no selection; default to current cityInput value
            }
        }

        // Set up update button
        updateButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val city = cityInput.text.toString().trim()

            // Basic validation
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || city.isEmpty()) {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateUserData(firstName, lastName, email, phone, city, selectedImageFile)
        }
    }
}