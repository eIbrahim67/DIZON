package com.eibrahim.dizon.auth.api

import com.eibrahim.dizon.details.model.PropertyDetails
import com.eibrahim.dizon.favorite.model.FavoritePropertiesResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PropertyApi {

    @GET("/api/Properties/GetById/{id}")
    suspend fun getPropertyById(@Path("id") id: Int): Response<PropertyDetails>

 }