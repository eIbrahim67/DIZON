package com.eibrahim.dizon.addproperty.viewmodel

class PropertyRepository {

    suspend fun addProperty(property: Property): Result<Unit> {
        return Result.success(Unit)
    }

    suspend fun getProperties(): Result<List<Property>> {
        return Result.success(emptyList())
    }
}