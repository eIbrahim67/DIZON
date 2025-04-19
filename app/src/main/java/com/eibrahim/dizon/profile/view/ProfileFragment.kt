package com.eibrahim.dizon.profile.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.google.android.material.button.MaterialButton

class ProfileFragment : Fragment() {


    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = findNavController()


        view.findViewById<ConstraintLayout>(R.id.add_property).setOnClickListener{
            navController.navigate(R.id.addPropertyFragment)
        }

        view.findViewById<ConstraintLayout>(R.id.edit_profile).setOnClickListener{
            navController.navigate(R.id.editProfileFragment)
        }

        view.findViewById<ConstraintLayout>(R.id.change_password).setOnClickListener{
            navController.navigate(R.id.changePasswordFragment)
        }

        view.findViewById<ConstraintLayout>(R.id.delete_account).setOnClickListener{
            navController.navigate(R.id.deleteAccountFragment)
        }

    }
}