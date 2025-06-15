package com.eibrahim.dizon.myProperty.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.myProperty.api.RetrofitMyProperty
import com.eibrahim.dizon.myProperty.model.MyPropertyResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class MyPropertyViewModel : ViewModel() {
    private val _properties = MutableLiveData<MyPropertyResponse?>()
    val properties: MutableLiveData<MyPropertyResponse?> = _properties

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadMyProperties() {
        viewModelScope.launch {
            try {
                val response: Response<MyPropertyResponse> =
                    RetrofitMyProperty.myPropertyApi.getMyProperties()

                if (response.isSuccessful) {
                    val propertyResponse = response.body()
                    _properties.value = propertyResponse
                } else {
                    // Handle the error response
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("Test3", "Error: $errorMessage")
                }
            } catch (e: Exception) {
                Log.e("Exception", ": ${e.message}")
            }
        }
    }
}