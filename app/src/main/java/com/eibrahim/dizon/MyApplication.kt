package com.eibrahim.dizon

import android.app.Application
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.chatbot.api.RetrofitChatbot
import com.eibrahim.dizon.home.api.RetrofitHome
import com.eibrahim.dizon.search.api.RetrofitSearch

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initAuthPreferences(this)
        RetrofitHome.initAuthPreferences(this)
        RetrofitSearch.initAuthPreferences(this)
        RetrofitChatbot.initAuthPreferences(this)
    }
}