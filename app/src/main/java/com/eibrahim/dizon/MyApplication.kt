package com.eibrahim.dizon

import android.app.Application
import com.eibrahim.dizon.auth.api.RetrofitClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initAuthPreferences(this)
    }
}