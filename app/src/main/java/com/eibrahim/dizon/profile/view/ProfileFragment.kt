package com.eibrahim.dizon.profile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.api.AuthApi
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.auth.api.UserResponse
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide // Import Glide for image loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var navController: NavController
    // Lazy initialization of AuthApi to access the API endpoint
    private val authApi: AuthApi by lazy { RetrofitClient.api }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // Fetch and display user profile data when the view is created
        fetchUserProfile(view)

        // Existing navigation listeners
        view.findViewById<ConstraintLayout>(R.id.add_property).setOnClickListener {
            navController.navigate(R.id.addPropertyFragment)
        }

        view.findViewById<ConstraintLayout>(R.id.edit_profile).setOnClickListener {
            navController.navigate(R.id.editProfileFragment)
        }

        view.findViewById<ConstraintLayout>(R.id.change_password).setOnClickListener {
            navController.navigate(R.id.changePasswordFragment)
        }

        view.findViewById<ConstraintLayout>(R.id.delete_account).setOnClickListener {
            navController.navigate(R.id.deleteAccountFragment)
        }
    }

    // Function to fetch user profile data from the API and update UI
    private fun fetchUserProfile(view: View) {
        // Use CoroutineScope with IO dispatcher for network calls
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the API call to get user data
                val response: Response<UserResponse> = authApi.getUser()
                // Switch to Main dispatcher to update UI
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        userResponse?.let {
                            // Update profile_name with firstName and lastName
                            val profileNameTextView = view.findViewById<TextView>(R.id.profile_name)
                            profileNameTextView.text = "${it.firstName} ${it.lastName}"

                            // Load imageUrl into profile_image using Glide
                            val profileImageView = view.findViewById<ImageView>(R.id.profile_image)
                            Glide.with(this@ProfileFragment)
                                .load(it.imageUrl) // URL from API response
                                .placeholder(R.drawable.man) // Default image while loading
                                .centerCrop()
                                .error(R.drawable.man) // Fallback image on error
                                .into(profileImageView) // Target ImageView
                        }
                    } else {
                        // Log or handle API error (e.g., 401 Unauthorized, 500 Server Error)
                        println("API Error: ${response.code()} - ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                // Handle network errors or other exceptions
                withContext(Dispatchers.Main) {
                    println("Network Exception: ${e.message}")
                }
            }
        }
    }
}