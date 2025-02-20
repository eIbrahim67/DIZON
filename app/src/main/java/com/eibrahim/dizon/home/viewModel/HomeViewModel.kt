package com.eibrahim.dizon.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.core.local.Llama
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.core.utils.UtilsFunctions.applyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel : ViewModel() {
    private val _currentDate = MutableLiveData<Response<String>>()
    val currentDate: LiveData<Response<String>> get() = _currentDate
    fun getCurrentDate() = applyResponse(_currentDate, viewModelScope) {
        SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH).format(Date())
    }

    private val _helloSate = MutableLiveData<Response<String>>()
    val helloSate: LiveData<Response<String>> get() = _helloSate
    fun getHelloSate() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        applyResponse(_helloSate, viewModelScope) {
            when (hour) {
                in 4..11 -> "Good morning"
                in 12..17 -> "Good afternoon"
                else -> "Good evening"
            }
        }
    }

}