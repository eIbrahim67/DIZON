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

}