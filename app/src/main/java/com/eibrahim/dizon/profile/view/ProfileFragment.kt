package com.eibrahim.dizon.profile.view

import android.content.Intent
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
import android.widget.Toast
import com.bumptech.glide.Glide
import com.eibrahim.dizon.auth.AuthActivity
import com.eibrahim.dizon.auth.AuthPreferences
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
    private val authApi: AuthApi by lazy { RetrofitClient.api }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // Fetch and display user profile data
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

        // Handle logout when the logout icon is clicked
        view.findViewById<ImageView>(R.id.logout_icon).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {

                    val authPreferences = AuthPreferences(requireContext())
                    authPreferences.clearToken()
                    startActivity(Intent(requireContext(), AuthActivity::class.java))
                    requireActivity().finish()


                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun fetchUserProfile(view: View) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<UserResponse> = authApi.getUser()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        userResponse?.let {
                            val profileNameTextView = view.findViewById<TextView>(R.id.profile_name)
                            profileNameTextView.text = "${it.firstName}\n${it.lastName}"

                            val profileImageView = view.findViewById<ImageView>(R.id.profile_image)
                            Glide.with(this@ProfileFragment)
                                .load(it.imageUrl)
                               // .placeholder(R.drawable.man)
                                .centerCrop()
                                .error(R.drawable.man)
                                .into(profileImageView)
                        }
                    } else {
                        println("API Error: ${response.code()} - ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Network Exception: ${e.message}")
                }

            }
        }
    }

}