package com.eibrahim.dizon.addproperty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.addproperty.model.AddPropertyData
import com.eibrahim.dizon.details.model.AmenitiesResponse
import com.eibrahim.dizon.details.model.PaymentIntentRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private val _clientSecret = MutableLiveData<String>()
    val clientSecret: LiveData<String> get() = _clientSecret

    fun fetchPaymentIntentClientSecret() {

        val request = PaymentIntentRequest(
            currency = "EGP",
            paymentIntentId = null // Set to a specific ID if required by your backend
        )

        viewModelScope.launch {
            _clientSecret.value = repository.fetchPaymentIntentClientSecret(request).clientSecret
        }
    }

    fun uploadProperty(addPropertyData: AddPropertyData) {

        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val location =
                "${addPropertyData.street}, ${addPropertyData.city}, ${addPropertyData.governate}"

            val property = Property(
                title = addPropertyData.title,
                description = addPropertyData.description,
                price = addPropertyData.price,
                location = location,
                locationUrl = addPropertyData.locationUrl,
                propertyType = addPropertyData.propertyType,
                listingType = addPropertyData.listingType,
                size = addPropertyData.size,
                beds = addPropertyData.beds,
                bathrooms = addPropertyData.bathrooms
            )

            val result = repository.addProperty(
                property,
                addPropertyData.imageFiles,
                addPropertyData.internalAmenityIds,
                addPropertyData.externalAmenityIds,
                addPropertyData.accessibilityAmenityIds
            )

            _isLoading.postValue(false)

            if (result.isSuccess) {
                _addSuccess.postValue(true)
            } else {
                _errorMessage.postValue("Failed to add property: ${result.exceptionOrNull()?.message}")
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