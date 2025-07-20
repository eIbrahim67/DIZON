package com.eibrahim.dizon.profile.view

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.AuthActivity
import com.eibrahim.dizon.auth.AuthPreferences
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.eibrahim.dizon.profile.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    //    private val viewModel: ProfileViewModel by viewModels()
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         viewModel.fetchUserData()
         
        val profileNameTextView = view.findViewById<TextView>(R.id.profile_name)
        val profileImageView = view.findViewById<ImageView>(R.id.profile_image)


        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                profileNameTextView.text = "${it.firstName} ${it.lastName}"
                Glide.with(this)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.man)
                    .error(R.drawable.man)
                    .circleCrop()
                    .into(profileImageView)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<ConstraintLayout>(R.id.my_property)
            .setOnClickListener { findNavController().navigate(R.id.MyPropertyFragment) }

        view.findViewById<ConstraintLayout>(R.id.add_property)
            .setOnClickListener { findNavController().navigate(R.id.addPropertyFragment) }

        view.findViewById<ConstraintLayout>(R.id.edit_profile)
            .setOnClickListener { findNavController().navigate(R.id.editProfileFragment) }

        view.findViewById<ConstraintLayout>(R.id.aboutUs)
            .setOnClickListener { findNavController().navigate(R.id.aboutUsFragment) }

        view.findViewById<ConstraintLayout>(R.id.change_password)
            .setOnClickListener { findNavController().navigate(R.id.changePasswordFragment) }

        view.findViewById<ConstraintLayout>(R.id.delete_account)
            .setOnClickListener { findNavController().navigate(R.id.deleteAccountFragment) }

        view.findViewById<ImageView>(R.id.logout_icon).setOnClickListener {
            AuthPreferences(requireContext()).clearToken()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }
}
