package com.eibrahim.dizon.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.eibrahim.dizon.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class AuthActivity : AppCompatActivity() {

    private var navController: NavController? = null
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navController = supportFragmentManager.findFragmentById(R.id.nav_auth)?.findNavController()
        Log.d("AuthActivity", "NavController initialized: ${navController != null}")

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("19324655100-27k3987q8sbg46u7566a2fqhoq8ks1ph.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        Log.d("AuthActivity", "Google Sign-In client initialized")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("AuthActivity", "onActivityResult called with requestCode: $requestCode, resultCode: $resultCode")
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("AuthActivity", "Google Sign-In successful, idToken: ${account.idToken}")
                navController?.currentDestination?.id?.let { destinationId ->
                    Log.d("AuthActivity", "Navigating to destination: $destinationId with idToken")
                    val bundle = Bundle().apply {
                        putString("googleIdToken", account.idToken)
                    }
                    navController?.navigate(destinationId, bundle)
                } ?: run {
                    Log.e("AuthActivity", "NavController or currentDestination is null")
                }
            } catch (e: ApiException) {
                Log.e("AuthActivity", "Google Sign-In failed: ${e.statusCode} - ${e.message}", e)
                navController?.currentDestination?.id?.let { destinationId ->
                    Log.d("AuthActivity", "Navigating to destination: $destinationId with error")
                    val bundle = Bundle().apply {
                        putString("error", "Google Sign-In failed: ${e.message} (Status: ${e.statusCode})")
                    }
                    navController?.navigate(destinationId, bundle)
                } ?: run {
                    Log.e("AuthActivity", "NavController or currentDestination is null")
                }
            } catch (e: Exception) {
                Log.e("AuthActivity", "Unexpected error in Google Sign-In: ${e.message}", e)
            }
        } else {
            Log.w("AuthActivity", "Unknown requestCode: $requestCode")
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }
}