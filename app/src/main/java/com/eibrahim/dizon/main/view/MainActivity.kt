package com.eibrahim.dizon.main.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.eibrahim.dizon.auth.AuthActivity
import com.eibrahim.dizon.R
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private var navController: NavController? = null

    private val navOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        if (!viewModel.isUserLogin())
            startActivity(Intent(this, AuthActivity::class.java))

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.navigateToFragment.observe(this) { fragmentId ->
            fragmentId?.let {
                navController?.navigate(fragmentId, null, navOptions)
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        navController =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment)
                ?.findNavController()

        navController?.let {
            bottomNavigationView.setupWithNavController(it)
        }

    }
}