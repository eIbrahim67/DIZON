package com.eibrahim.dizon.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eibrahim.dizon.addproperty.viewmodel.PropertyRepository

class PaymentViewModelFactory (

    private val repository: PropertyRepository

    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(PaymentViewModel::class.java))
                return PaymentViewModel(repository) as T
            else
                throw IllegalArgumentException("Unknown view model")

        }

    }