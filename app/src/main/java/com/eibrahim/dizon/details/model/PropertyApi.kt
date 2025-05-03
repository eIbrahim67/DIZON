package com.eibrahim.dizon.details.model

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PropertyApi {

    @GET("/api/Properties/GetById/{id}")
    suspend fun getPropertyById(@Path("id") id: Int): Response<PropertyDetails>

 }