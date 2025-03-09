package com.eibrahim.dizon.auth.register.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _registrationState = MutableLiveData<Result<Boolean>>()
    val registrationState: LiveData<Result<Boolean>> get() = _registrationState

    fun registerUser(name: String, email: String, phone: String, city: String, password: String) {
        if (name.isBlank() || email.isBlank() || phone.isBlank() || city.isBlank() || password.isBlank()) {
            _registrationState.value = Result.failure(Exception("All fields must be filled"))
            return
        }

        viewModelScope.launch {
            try {
                // Simulate API call (Replace with actual repository call)
                val isSuccess = registerUserOnServer(name, email, phone, city, password)
                _registrationState.value = Result.success(isSuccess)
            } catch (e: Exception) {
                _registrationState.value = Result.failure(e)
            }
        }
    }

    private suspend fun registerUserOnServer(name: String, email: String, phone: String, city: String, password: String): Boolean {
        // Simulate network delay
        kotlinx.coroutines.delay(2000)
        return true // Assume registration is successful
    }
}