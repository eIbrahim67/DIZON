package com.eibrahim.dizon.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.addproperty.model.AddPropertyData
import com.eibrahim.dizon.addproperty.viewmodel.Property
import com.eibrahim.dizon.addproperty.viewmodel.PropertyRepository
import kotlinx.coroutines.launch
import java.io.File

class PaymentViewModel(private val repository: PropertyRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _addSuccess = MutableLiveData<Boolean>()
    val addSuccess: LiveData<Boolean> get() = _addSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    fun addProperty(addPropertyData: AddPropertyData) {

        _isLoading.value = true

        viewModelScope.launch {
            val location = "${addPropertyData.street}, ${addPropertyData.city}, ${addPropertyData.governate}"

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

            _isLoading.value = false

            if (result.isSuccess) {
                _addSuccess.value = true
            } else {
                _errorMessage.value = "Failed to add property: ${result.exceptionOrNull()?.message}"
            }
        }
    }

}