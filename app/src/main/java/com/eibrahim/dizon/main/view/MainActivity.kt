package com.eibrahim.dizon.main.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.eibrahim.dizon.R
import com.eibrahim.dizon.auth.AuthActivity
import com.eibrahim.dizon.auth.AuthPreferences
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.chatbot.api.RetrofitChatbot
import com.eibrahim.dizon.home.api.RetrofitHome
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.eibrahim.dizon.myProperty.api.RetrofitMyProperty
import com.eibrahim.dizon.propertyResults.api.RetrofitResult
import com.eibrahim.dizon.search.api.RetrofitSearch
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

        // Check if user is logged in
        val authPreferences = AuthPreferences(this)
        val token = authPreferences.getToken()
        if (token.isNullOrEmpty()) {
            Log.d("MainActivity", "User is not logged in, navigating to AuthActivity")
            val isFirstLaunch = authPreferences.isFirstLaunch()
            val startDestination = if (isFirstLaunch) R.id.splashFragment else R.id.loginFragment
            val intent = Intent(this, AuthActivity::class.java).apply {
                putExtra("start_destination", startDestination)
            }
            startActivity(intent)
            finish()
            return
        }

        // Proceed with loading UI only if user is logged in
        RetrofitClient.initAuthPreferences(this)
        Log.d("MainActivity", "RetrofitClient initialized")

        RetrofitHome.initAuthPreferences(this)
        RetrofitSearch.initAuthPreferences(this)
        RetrofitChatbot.initAuthPreferences(this)
        RetrofitMyProperty.initAuthPreferences(this)
        RetrofitResult.initAuthPreferences(this)

        // جلب بيانات المستخدم مبكرًا
        viewModel.fetchUserData()

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Observe navigation events
        viewModel.navigateToFragment.observe(this) { fragmentId ->
            fragmentId?.let {
                Log.d("MainActivity", "Navigating to fragment ID: $it")
                navController?.navigate(it, null, navOptions)
            }
        }

        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        navController = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment)
            ?.findNavController()

        navController?.let {
            bottomNavigationView.setupWithNavController(it)
            Log.d("MainActivity", "BottomNavigationView setup with NavController")
        } ?: run {
            Log.e("MainActivity", "NavController is null")
        }
    }
}