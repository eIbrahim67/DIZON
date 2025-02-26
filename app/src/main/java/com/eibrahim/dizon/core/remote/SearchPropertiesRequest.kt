package com.eibrahim.dizon.core.remote

data class SearchPropertiesRequest(
    val action: String = "search_properties",
    val parameters: SearchPropertiesParameters
)