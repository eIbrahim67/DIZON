package com.eibrahim.dizon.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.addproperty.model.AddPropertyData
import com.eibrahim.dizon.addproperty.viewmodel.Property
import com.eibrahim.dizon.addproperty.viewmodel.PropertyRepository
import com.eibrahim.dizon.payment.model.PaymentInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentViewModel(private val repository: PropertyRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _addSuccess = MutableLiveData<Boolean>()
    val addSuccess: LiveData<Boolean> get() = _addSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var correctPaymentInfo = PaymentInfo(
        cardHolderName = "Habiba Mostafa",
        cardNumber = "111111111111111111111111",
        expiryMonth = "1",
        expiryYear = "30",
        securityCode = "123",
        cardType = "Visa"
    )

    private var notEnoughPaymentInfo = PaymentInfo(
        cardHolderName = "Mohamed Ahmed",
        cardNumber = "4111111111111111", // typical valid Visa test card
        expiryMonth = "12",
        expiryYear = "27",
        securityCode = "123",
        cardType = "Visa"
    )


    fun checkPaymentInfo(input: PaymentInfo) =
        when (input) {
            correctPaymentInfo -> { 0 }
            notEnoughPaymentInfo -> { 1 }
            else -> 2
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