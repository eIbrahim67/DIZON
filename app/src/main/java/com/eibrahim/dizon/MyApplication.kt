package com.eibrahim.dizon

import android.app.Application
import com.eibrahim.dizon.auth.api.RetrofitClient
import com.eibrahim.dizon.chatbot.api.RetrofitChatbot
import com.eibrahim.dizon.home.api.RetrofitHome
import com.eibrahim.dizon.myProperty.api.RetrofitMyProperty
import com.eibrahim.dizon.search.api.RetrofitSearch
import com.stripe.android.PaymentConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initAuthPreferences(this)
        RetrofitHome.initAuthPreferences(this)
        RetrofitSearch.initAuthPreferences(this)
        RetrofitChatbot.initAuthPreferences(this)
        RetrofitMyProperty.initAuthPreferences(this)

        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51RXPxIRHB8EWOr5UtIiT6dVRn7j68SFzmO6kKiJpfLoh58o9kj5h3kb8QTpvnhaqLOVPB5ladvNBHHrQrR2Q9XdH00Q2gtWAQ5" // Replace with your real publishable key
        )

    }
}