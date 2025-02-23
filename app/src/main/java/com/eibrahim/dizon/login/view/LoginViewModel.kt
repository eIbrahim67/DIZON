package com.eibrahim.dizon.login.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val _loginState = MutableLiveData<Result<Boolean>>()
    val loginState: LiveData<Result<Boolean>> = _loginState

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = Result.failure(Exception("Please fill in all fields"))
            return
        }

        // Simulate authentication (Replace this with Firebase/Auth API)
        if (email == "moamen@gmail.com" && password == "123456") {
            _loginState.value = Result.success(true)
        } else {
            _loginState.value = Result.failure(Exception("Invalid email or password"))
        }
    }
}