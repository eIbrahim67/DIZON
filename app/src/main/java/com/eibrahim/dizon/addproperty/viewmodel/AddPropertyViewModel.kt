package com.eibrahim.dizon.addproperty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.details.model.AmenitiesResponse
import kotlinx.coroutines.launch
import java.io.File

class AddPropertyViewModel(private val repository: PropertyRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _addSuccess = MutableLiveData<Boolean>()
    val addSuccess: LiveData<Boolean> get() = _addSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _internalAmenities = MutableLiveData<AmenitiesResponse>()
    val internalAmenities: LiveData<AmenitiesResponse> get() = _internalAmenities

    private val _externalAmenities = MutableLiveData<AmenitiesResponse>()
    val externalAmenities: LiveData<AmenitiesResponse> get() = _externalAmenities

    private val _accessibilityAmenities = MutableLiveData<AmenitiesResponse>()
    val accessibilityAmenities: LiveData<AmenitiesResponse> get() = _accessibilityAmenities

    init {
        fetchAmenities()
    }

    private fun fetchAmenities() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val internalResult = repository.getInternalAmenities()
                if (internalResult.isSuccess) {
                    _internalAmenities.value = internalResult.getOrThrow()
                } else {
                    _errorMessage.value = "Failed to load internal amenities"
                }

                val externalResult = repository.getExternalAmenities()
                if (externalResult.isSuccess) {
                    _externalAmenities.value = externalResult.getOrThrow()
                } else {
                    _errorMessage.value = "Failed to load external amenities"
                }

                val accessibilityResult = repository.getAccessibilityAmenities()
                if (accessibilityResult.isSuccess) {
                    _accessibilityAmenities.value = accessibilityResult.getOrThrow()
                } else {
                    _errorMessage.value = "Failed to load accessibility amenities"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error loading amenities: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProperty(
        title: String,
        description: String,
        price: Double,
        street: String,
        city: String,
        governate: String,
        locationUrl: String,
        propertyType: String,
        listingType: String,
        size: Int,
        rooms: Int,
        beds: Int,
        bathrooms: Int,
        imageFiles: List<File>,
        internalAmenityIds: List<Int>,
        externalAmenityIds: List<Int>,
        accessibilityAmenityIds: List<Int>
    ) {
        if (title.isBlank() || description.isBlank() || street.isBlank() || city.isBlank() || governate.isBlank() || locationUrl.isBlank() || price <= 0 || imageFiles.isEmpty()) {
            _errorMessage.value = "Please fill all required fields, including Street, City, Governate, Location URL, and upload at least one image."
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            val location = "$street, $city, $governate"
            val property = Property(
                title = title,
                description = description,
                price = price,
                location = location,
                locationUrl = locationUrl,
                propertyType = propertyType,
                listingType = listingType,
                size = size,
                beds = beds,
                bathrooms = bathrooms
            )

            val result = repository.addProperty(property, imageFiles, internalAmenityIds, externalAmenityIds, accessibilityAmenityIds)
            _isLoading.value = false
            if (result.isSuccess) {
                _addSuccess.value = true
            } else {
                _errorMessage.value = "Failed to add property: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}

class AddPropertyViewModelFactory(
    private val repository: PropertyRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPropertyViewModel::class.java)) {
            return AddPropertyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}