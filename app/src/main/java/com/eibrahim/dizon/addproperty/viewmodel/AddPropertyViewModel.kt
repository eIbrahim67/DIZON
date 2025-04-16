package com.eibrahim.dizon.addproperty.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddPropertyViewModel(private val repository: PropertyRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _addSuccess = MutableLiveData<Boolean>()
    val addSuccess: LiveData<Boolean> get() = _addSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun addProperty(
        title: String,
        description: String,
        price: Double,
        location: String,
        type: String,
        size: Int,
        rooms: Int,
        beds: Int,
        bathrooms: Int,
        amenities: List<String>,
        images: List<String>
    ) {
        if (title.isBlank() || description.isBlank() || location.isBlank() || price <= 0 || images.isEmpty()) {
            _errorMessage.value = "Please fill all required fields and upload at least one image."
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            val property = Property(
                propertyId = "id_${System.currentTimeMillis()}",
                ownerId = "current_user_id",
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
                images = images
            )

            val result = repository.addProperty(property)
            _isLoading.value = false
            if (result.isSuccess) {
                _addSuccess.value = true
            } else {
                _errorMessage.value = "Failed to add property. Please try again."
            }
        }
    }
}