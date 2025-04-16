package com.eibrahim.dizon.deleteaccount.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeleteAccountViewModel : ViewModel() {

    private val _showConfirmationDialog = MutableLiveData<Boolean>()
    val showConfirmationDialog: LiveData<Boolean> get() = _showConfirmationDialog

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> get() = _navigateBack

    private val _deleteCompleted = MutableLiveData<Boolean>()
    val deleteCompleted: LiveData<Boolean> get() = _deleteCompleted

    fun onDeleteAccountClicked() {
        _showConfirmationDialog.value = true
    }

    fun onGoBackClicked() {
        _navigateBack.value = true
    }

    fun onConfirmDelete() {
        _showConfirmationDialog.value = false
        _isLoading.value = true

        viewModelScope.launch {
            delay(2000)
            _isLoading.value = false
            _deleteCompleted.value = true
        }
    }

    fun onCancelDelete() {
        _showConfirmationDialog.value = false
    }
}