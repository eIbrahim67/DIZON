package com.eibrahim.dizon.addproperty.view

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.addproperty.model.AddPropertyData
import com.eibrahim.dizon.addproperty.viewmodel.AddPropertyViewModel
import com.eibrahim.dizon.addproperty.viewmodel.AddPropertyViewModelFactory
import com.eibrahim.dizon.addproperty.viewmodel.PropertyRepository
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import java.io.File
import java.io.FileOutputStream

class AddPropertyFragment : Fragment() {

    private lateinit var viewModel: AddPropertyViewModel
    private val imageUris = mutableListOf<Uri>()
    private val imageFiles = mutableListOf<File>()
    private val imageViews = mutableListOf<View>() // Track image views for deletion
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var clientSecret: String
    private lateinit var publishableKey: String

    // UI elements
    private lateinit var btnback: ImageView
    private lateinit var imagesScrollView: HorizontalScrollView
    private lateinit var imagesContainer: LinearLayout
    private lateinit var addImageButton: Button
    private lateinit var typeSpinner: Spinner
    private lateinit var listingTypeSpinner: Spinner
    private lateinit var priceInput: TextInputEditText
    private lateinit var titleInput: TextInputEditText
    private lateinit var sizeInput: TextInputEditText
    private lateinit var roomsInput: TextInputEditText
    private lateinit var bedsInput: TextInputEditText
    private lateinit var bathroomsInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var internalAmenitiesChipGroup: ChipGroup
    private lateinit var externalAmenitiesChipGroup: ChipGroup
    private lateinit var accessibilityAmenitiesChipGroup: ChipGroup
    private lateinit var streetInput: TextInputEditText
    private lateinit var cityInput: TextInputEditText
    private lateinit var governateInput: TextInputEditText
    private lateinit var locationUrlInput: TextInputEditText
    private lateinit var submitButton: Button

    lateinit var propertyData: AddPropertyData

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data?.clipData != null) {
                    // Multiple images selected
                    for (i in 0 until data.clipData!!.itemCount) {
                        val uri = data.clipData!!.getItemAt(i).uri
                        processImage(uri)
                    }
                } else if (data?.data != null) {
                    // Single image selected
                    processImage(data.data!!)
                }
            }
        }

    private fun processImage(uri: Uri) {
        val mimeType = requireContext().contentResolver.getType(uri)
        if (!isSupportedImageFormat(mimeType)) {
            Toast.makeText(
                requireContext(),
                "Unsupported image format. Use .jpg, .jpeg, .png, or .gif",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                val size = it.getLong(sizeIndex)
                if (size > 5 * 1024 * 1024) {
                    Toast.makeText(
                        requireContext(),
                        "Image size exceeds 5MB limit",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
            }
        }

        // Convert URI to File
        val file = uriToFile(uri)
        if (file != null) {
            imageUris.add(uri)
            imageFiles.add(file)
            addImageView(uri, file)
        } else {
            Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_LONG).show()
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

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "image_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    private fun addImageView(uri: Uri, file: File) {
        // Create FrameLayout to hold ImageView and delete icon
        val frameLayout = FrameLayout(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(dpToPx(10f), dpToPx(10f), dpToPx(10f), dpToPx(10f))
            }
        }

        // Create ImageView for the image
        val imageView = ImageView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(dpToPx(120f), dpToPx(160f))
            setImageURI(uri)
            scaleType = ImageView.ScaleType.CENTER_CROP
            contentDescription = "Uploaded image"
        }

        // Create ImageView for the delete icon
        var deleteIcon = ImageView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(dpToPx(40f), dpToPx(40f)).apply {
                gravity = android.view.Gravity.TOP or android.view.Gravity.END
                setMargins(0, dpToPx(8f), dpToPx(8f), 0)
            }
            setImageResource(R.drawable.ic_delete)
            contentDescription = "Delete image"
            setOnClickListener {
                // Remove image from lists and UI
                val index = imageUris.indexOf(uri)
                if (index != -1) {
                    imageUris.removeAt(index)
                    imageFiles.removeAt(index)
                    imagesContainer.removeView(frameLayout)
                    imageViews.remove(frameLayout)
                    // Hide imagesScrollView if no images remain
                    if (imageUris.isEmpty()) {
                        imagesScrollView.visibility = View.GONE
                    }
                }
            }
        }

        // Add ImageView and delete icon to FrameLayout
        frameLayout.addView(imageView)
        frameLayout.addView(deleteIcon)

        // Add FrameLayout to imagesContainer and track it
        imagesContainer.addView(frameLayout)
        imageViews.add(frameLayout)

        // Show imagesScrollView when an image is added
        imagesScrollView.visibility = View.VISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.GONE

        // Initialize UI elements
        btnback = view.findViewById(R.id.btnback)
        imagesScrollView = view.findViewById(R.id.imagesScrollView)
        imagesContainer = view.findViewById(R.id.imagesContainer)
        addImageButton = view.findViewById(R.id.addImageButton)
        typeSpinner = view.findViewById(R.id.typeSpinner)
        listingTypeSpinner = view.findViewById(R.id.listingTypeSpinner)
        priceInput = view.findViewById(R.id.priceInput)
        titleInput = view.findViewById(R.id.titleInput)
        sizeInput = view.findViewById(R.id.sizeInput)
        roomsInput = view.findViewById(R.id.roomsInput)
        bedsInput = view.findViewById(R.id.bedsInput)
        bathroomsInput = view.findViewById(R.id.bathroomsInput)
        descriptionInput = view.findViewById(R.id.descriptionInput)
        internalAmenitiesChipGroup = view.findViewById(R.id.internalAmenitiesChipGroup)
        externalAmenitiesChipGroup = view.findViewById(R.id.externalAmenitiesChipGroup)
        accessibilityAmenitiesChipGroup = view.findViewById(R.id.accessibilityAmenitiesChipGroup)
        streetInput = view.findViewById(R.id.streetInput)
        cityInput = view.findViewById(R.id.cityInput)
        governateInput = view.findViewById(R.id.governateInput)
        locationUrlInput = view.findViewById(R.id.locationUrlInput)
        submitButton = view.findViewById(R.id.submitButton)

        // Set up back button click listener
        btnback.setOnClickListener {
            findNavController().popBackStack()
        }

        // Hide imagesScrollView initially
        imagesScrollView.visibility = View.GONE

        // Initialize the repository
        val repository = PropertyRepository()
        val factory = AddPropertyViewModelFactory(repository)

        // Initialize ViewModel with factory
        viewModel = ViewModelProvider(this, factory).get(AddPropertyViewModel::class.java)

        // Set up Spinner adapters
        val propertyTypes = arrayOf("Apartment", "House", "Condo", "Villa")
        val listingTypes = arrayOf("For Sale", "For Rent")
        val propertyAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, propertyTypes)
        val listingAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listingTypes)
        propertyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = propertyAdapter
        listingTypeSpinner.adapter = listingAdapter
        typeSpinner.setSelection(0)
        listingTypeSpinner.setSelection(0)

        // Load Amenities with enhanced chip styling
        viewModel.internalAmenities.observe(viewLifecycleOwner) { response ->
            internalAmenitiesChipGroup.removeAllViews()
            response.`$values`.forEach { amenity ->
                val chip = Chip(requireContext()).apply {
                    text = amenity.name
                    isCheckable = true
                    id = amenity.amenityId
                    setChipBackgroundColorResource(R.color.chip_background_selector)
                    setTextColor(resources.getColorStateList(R.color.chip_text_selector, null))
                    chipStrokeColor =
                        resources.getColorStateList(R.color.chip_stroke_selector, null)
                    chipStrokeWidth = if (isChecked) 2f else 1f
                }
                internalAmenitiesChipGroup.addView(chip)
            }
        }

        viewModel.externalAmenities.observe(viewLifecycleOwner) { response ->
            externalAmenitiesChipGroup.removeAllViews()
            response.`$values`.forEach { amenity ->
                val chip = Chip(requireContext()).apply {
                    text = amenity.name
                    isCheckable = true
                    id = amenity.amenityId
                    setChipBackgroundColorResource(R.color.chip_background_selector)
                    setTextColor(resources.getColorStateList(R.color.chip_text_selector, null))
                    chipStrokeColor =
                        resources.getColorStateList(R.color.chip_stroke_selector, null)
                    chipStrokeWidth = if (isChecked) 2f else 1f
                }
                externalAmenitiesChipGroup.addView(chip)
            }
        }

        viewModel.accessibilityAmenities.observe(viewLifecycleOwner) { response ->
            accessibilityAmenitiesChipGroup.removeAllViews()
            response.`$values`.forEach { amenity ->
                val chip = Chip(requireContext()).apply {
                    text = amenity.name
                    isCheckable = true
                    id = amenity.amenityId
                    setChipBackgroundColorResource(R.color.chip_background_selector)
                    setTextColor(resources.getColorStateList(R.color.chip_text_selector, null))
                    chipStrokeColor =
                        resources.getColorStateList(R.color.chip_stroke_selector, null)
                    chipStrokeWidth = if (isChecked) 2f else 1f
                }
                accessibilityAmenitiesChipGroup.addView(chip)
            }
        }

        addImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            pickImageLauncher.launch(intent)
        }

        PaymentConfiguration.init(
            requireContext(),
            "pk_test_51RXPxIRHB8EWOr5UtIiT6dVRn7j68SFzmO6kKiJpfLoh58o9kj5h3kb8QTpvnhaqLOVPB5ladvNBHHrQrR2Q9XdH00Q2gtWAQ5"
        )

        publishableKey =
            "pk_test_51RXPxIRHB8EWOr5UtIiT6dVRn7j68SFzmO6kKiJpfLoh58o9kj5h3kb8QTpvnhaqLOVPB5ladvNBHHrQrR2Q9XdH00Q2gtWAQ5" // Replace with your Stripe publishable key
        // Initialize PaymentSheet
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)


        viewModel.fetchPaymentIntentClientSecret()
        viewModel.clientSecret.observe(viewLifecycleOwner) { data ->
            clientSecret = data
        }

        submitButton.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val price = priceInput.text.toString().toDoubleOrNull() ?: 0.0
            val street = streetInput.text.toString().trim()
            val city = cityInput.text.toString().trim()
            val governate = governateInput.text.toString().trim()
            val locationUrl = locationUrlInput.text.toString()
            val propertyType = typeSpinner.selectedItem?.toString() ?: "Apartment"
            val listingType = listingTypeSpinner.selectedItem?.toString() ?: "For Sale"
            val size = sizeInput.text.toString().toIntOrNull() ?: 0
            val rooms = roomsInput.text.toString().toIntOrNull() ?: 0
            val beds = bedsInput.text.toString().toIntOrNull() ?: 0
            val bathrooms = bathroomsInput.text.toString().toIntOrNull() ?: 0

            // Validate location fields
            if (street.isBlank() || city.isBlank() || governate.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter Street, City, and Governate",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Validate locationUrl
            if (locationUrl.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid Location URL",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val internalAmenityIds = mutableListOf<Int>()
            for (i in 0 until internalAmenitiesChipGroup.childCount) {
                val chip = internalAmenitiesChipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    internalAmenityIds.add(chip.id)
                }
            }

            val externalAmenityIds = mutableListOf<Int>()
            for (i in 0 until externalAmenitiesChipGroup.childCount) {
                val chip = externalAmenitiesChipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    externalAmenityIds.add(chip.id)
                }
            }

            val accessibilityAmenityIds = mutableListOf<Int>()
            for (i in 0 until accessibilityAmenitiesChipGroup.childCount) {
                val chip = accessibilityAmenitiesChipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    accessibilityAmenityIds.add(chip.id)
                }
            }

            if (title.isBlank() || description.isBlank() || street.isBlank() || city.isBlank() || governate.isBlank() || locationUrl.isBlank() || price <= 0 || imageFiles.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please fill all required fields, including Street, City, Governate, Location URL, and upload at least one image.",
                    Toast.LENGTH_LONG
                )

            } else {

                propertyData = AddPropertyData(
                    title = title,
                    description = description,
                    price = price,
                    street = street,
                    city = city,
                    governate = governate,
                    locationUrl = locationUrl,
                    propertyType = propertyType,
                    listingType = listingType,
                    size = size,
                    rooms = rooms,
                    beds = beds,
                    bathrooms = bathrooms,
                    imageFiles = imageFiles,
                    internalAmenityIds = internalAmenityIds,
                    externalAmenityIds = externalAmenityIds,
                    accessibilityAmenityIds = accessibilityAmenityIds
                )

                viewModel.fetchPaymentIntentClientSecret()
                initiatePayment()
            }

            viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
                submitButton.isEnabled = !isLoading
            }

            viewModel.addSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Property added successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    findNavController().popBackStack()
                }
            }

            viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(context, "Payment canceled", Toast.LENGTH_SHORT).show()
            }

            is PaymentSheetResult.Failed -> {
                Toast.makeText(
                    context,
                    "Payment failed: ${paymentSheetResult.error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            is PaymentSheetResult.Completed -> {
                Toast.makeText(context, "Payment successful!", Toast.LENGTH_SHORT).show()
                // Handle successful payment (e.g., update UI, notify backend)
                viewModel.uploadProperty(propertyData)
            }

        }
    }


    private fun initiatePayment() {
        // Configure PaymentSheet with customer configuration if needed
        val configuration = PaymentSheet.Configuration(
            merchantDisplayName = "Your Merchant Name",
            // Add customer configuration if using customer IDs
            // customer = PaymentSheet.CustomerConfiguration(customerId, ephemeralKey),
            allowsDelayedPaymentMethods = true
        )

        // Present PaymentSheet
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret = clientSecret,
            configuration = configuration
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        bottomNavigationView.visibility = View.VISIBLE
    }

    private lateinit var bottomNavigationView: BottomNavigationView

}