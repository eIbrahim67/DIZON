package com.eibrahim.dizon.auth.forgetpassword.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForgetPasswordViewModel : ViewModel() {
    private val _resetPasswordState = MutableLiveData<Result<Boolean>>()
    val resetPasswordState: LiveData<Result<Boolean>> get() = _resetPasswordState

    fun sendPasswordReset(email: String) {
        if (email.isEmpty()) {
            _resetPasswordState.value = Result.failure(Exception("Email cannot be empty"))
            return
        }
        // Simulate sending a password reset link
        _resetPasswordState.value = Result.success(true)
    }
}