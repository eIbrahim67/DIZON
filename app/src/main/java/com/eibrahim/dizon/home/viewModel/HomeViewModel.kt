package com.eibrahim.dizon.home.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.home.api.RetrofitHome
import com.eibrahim.dizon.home.model.RecommendedPropertyResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _properties = MutableLiveData<RecommendedPropertyResponse?>()
    val properties: MutableLiveData<RecommendedPropertyResponse?> = _properties

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var pageIndex = 1
    private val pageSize = 5

    fun loadRecommendations() {
        Log.d("Test1", "1")
        viewModelScope.launch {
            try {
                val response: Response<RecommendedPropertyResponse> =
                    RetrofitHome.homeApi.getRecommendedProperties(pageIndex, pageSize)

                if (response.isSuccessful) {
                    val propertyResponse = response.body()
                    _properties.value = propertyResponse
                    // Handle the successful response, e.g., update LiveData or StateFlow
                    Log.d("Test2", "Success: $propertyResponse")
                    // Example: _properties.value = propertyResponse?.data?.values
                } else {
                    // Handle the error response
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("Test3", "Error: $errorMessage")
                }
            } catch (e: Exception) {
                // Handle network or other exceptions
                Log.e("Exception", ": ${e.message}")
            }
        }
    }
}