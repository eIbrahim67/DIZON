package com.eibrahim.dizon.addproperty.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.addproperty.viewmodel.AddPropertyViewModel
import com.eibrahim.dizon.addproperty.viewmodel.AddPropertyViewModelFactory
import com.eibrahim.dizon.addproperty.viewmodel.PropertyRepository
import com.eibrahim.dizon.databinding.FragmentAddPropertyBinding
import com.google.android.material.chip.Chip

class AddPropertyFragment : Fragment() {

    private var _binding: FragmentAddPropertyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddPropertyViewModel
    private val imageUris = mutableListOf<Uri>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val mimeType = requireContext().contentResolver.getType(uri)
                if (!isSupportedImageFormat(mimeType)) {
                    Toast.makeText(requireContext(), "Unsupported image format. Use .jpg, .jpeg, .png, or .gif", Toast.LENGTH_LONG).show()
                    return@let
                }

                val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                        val size = it.getLong(sizeIndex)
                        if (size > 5 * 1024 * 1024) {
                            Toast.makeText(requireContext(), "Image size exceeds 5MB limit", Toast.LENGTH_LONG).show()
                            return@let
                        }
                    }
                }

                imageUris.add(uri)
                val imageView = ImageView(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(100, 100)
                    setImageURI(uri)
                    setPadding(8, 8, 8, 8)
                }
                binding.imagesContainer.addView(imageView)
            }
        }
    }

    private fun isSupportedImageFormat(mimeType: String?): Boolean {
        return mimeType in listOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the repository (replace with actual implementation)
        val repository = PropertyRepository() // TODO: Provide proper instance
        val factory = AddPropertyViewModelFactory(repository)

        // Initialize ViewModel with factory
        viewModel = ViewModelProvider(this, factory).get(AddPropertyViewModel::class.java)

        // Set up Spinner adapter
        val propertyTypes = arrayOf("Apartment", "House", "Condo", "Villa") // Example types
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, propertyTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeSpinner.adapter = adapter
        binding.typeSpinner.setSelection(0) // Set default selection

        binding.addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        binding.submitButton.setOnClickListener {

           /* val title = binding.titleInput.text.toString()
            val description = binding.descriptionInput.text.toString()
            val price = binding.priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val location = binding.locationInput.text.toString()
            val type = binding.typeSpinner.selectedItem?.toString() ?: "Apartment" // Fallback to default
            val size = binding.sizeInput.text.toString().toIntOrNull() ?: 0
            val rooms = binding.roomsInput.text.toString().toIntOrNull() ?: 0
            val beds = binding.bedsInput.text.toString().toIntOrNull() ?: 0
            val bathrooms = binding.bathroomsInput.text.toString().toIntOrNull() ?: 0

            val amenities = mutableListOf<String>()
            for (i in 0 until binding.amenitiesChipGroup.childCount) {
                val chip = binding.amenitiesChipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    amenities.add(chip.text.toString())
                }
            }

            val imageStrings = imageUris.map { it.toString() }

            viewModel.addProperty(
                title = title,
                description = description,
                price = price,
                location = location,
                type = type,
                size = size,
                rooms = rooms,
                beds = beds,
                bathrooms = bathrooms,
                amenities = amenities,
                images = imageStrings
            )*/

            findNavController().navigate(R.id.paymentFragment)
        }

       /* viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.submitButton.isEnabled = !isLoading
        }

        viewModel.addSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Property added successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.paymentFragment)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }*/
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/
}