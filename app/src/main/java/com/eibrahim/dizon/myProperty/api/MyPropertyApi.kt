package com.eibrahim.dizon.myProperty.api

import com.eibrahim.dizon.home.model.RecommendedPropertyResponse
import com.eibrahim.dizon.myProperty.model.MyPropertyResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyPropertyApi {

    @GET("/api/Properties/get-user-properties")
    suspend fun getMyProperties(): Response<MyPropertyResponse>



}