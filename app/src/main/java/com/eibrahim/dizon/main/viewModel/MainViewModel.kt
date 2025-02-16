package com.eibrahim.dizon.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(
) : ViewModel() {

    private val _navigateToFragment = MutableLiveData<Int?>()
    val navigateToFragment: LiveData<Int?> get() = _navigateToFragment
    fun navigateTo(fragmentName: Int?) {
        _navigateToFragment.value = fragmentName
    }
}