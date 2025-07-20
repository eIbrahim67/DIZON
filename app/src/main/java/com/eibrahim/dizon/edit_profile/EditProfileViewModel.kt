package com.eibrahim.dizon.edit_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eibrahim.dizon.auth.api.AuthApi
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.auth.api.UpdateResponse
import com.eibrahim.dizon.auth.api.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class EditProfileViewModel : ViewModel() {
    // Retrofit API instance
    private val authApi: AuthApi = RetrofitClient.api

    // LiveData to hold user profile data
    private val _userData = MutableLiveData<UserResponse?>()
    val userData: LiveData<UserResponse?> = _userData

    // LiveData to hold loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to hold error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    public val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    // Fetch user profile data from the API
    fun fetchUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authApi.getUser()
                if (response.isSuccessful) {
                    _userData.value = response.body()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Failed to fetch user data: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching user data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Function to download image from URL and save as File
    private suspend fun downloadImageFromUrl(imageUrl: String, cacheDir: File): File? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val tempFile = File(cacheDir, "current_profile_image_${System.currentTimeMillis()}.jpg")

                    FileOutputStream(tempFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }

                    inputStream.close()
                    connection.disconnect()

                    tempFile
                } else {
                    connection.disconnect()
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Update user profile data via the API
    fun updateUserData(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        city: String,
        imageFile: File?,
        cacheDir: File
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Convert strings to RequestBody for multipart form
                val firstNamePart = firstName.toRequestBody("text/plain".toMediaTypeOrNull())
                val lastNamePart = lastName.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneNumberPart = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                val cityPart = city.toRequestBody("text/plain".toMediaTypeOrNull())
                val imageUrlPart = "".toRequestBody("text/plain".toMediaTypeOrNull())

                // Determine which image file to use
                val finalImageFile = when {

                    imageFile != null -> imageFile

                    // Download the image if it's not already downloaded
                    !userData.value?.imageUrl.isNullOrEmpty() -> {
                        downloadImageFromUrl(userData.value!!.imageUrl!!, cacheDir)
                    }

                    else -> null
                }

                // Create image part if we have an image file
                val imagePart = finalImageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("Image", it.name, requestFile)
                }

                val response = authApi.updateUser(
                    firstNamePart,
                    lastNamePart,
                    emailPart,
                    phoneNumberPart,
                    cityPart,
                    imageUrlPart,
                    imagePart
                )

                if (response.isSuccessful) {
                    _errorMessage.value = null
                    // Clean up temporary file if it was downloaded
                    if (imageFile == null && finalImageFile != null && finalImageFile.exists()) {
                        finalImageFile.delete()
                    }
                    fetchUserData() // Refresh user data
                    _successMessage.value = "User data updated successfully"
                } else {
                    _errorMessage.value = "Failed to update user data: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating user data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}