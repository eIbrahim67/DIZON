package com.eibrahim.dizon.favorite.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FavoriteRepository {
    private val favoriteList = MutableLiveData<List<FavoriteProperty>>()

    init {
        // Dummy data
        favoriteList.value = listOf(
            FavoriteProperty(1, "https://www.propertyfinder.ae/property/d5596f824e7b2fe3d8e4820fb91ec05e/416/272/MODE/0df2db/13635501-e8d76o.webp?ctr=ae", "Luxury Villa", "15,000,000 LE", "Maadi, Cairo"),
            FavoriteProperty(2, "https://www.propertyfinder.ae/property/aabc8ff45bd7d606b615a23f3adf928e/416/272/MODE/35a74d/13614520-ffa59o.webp?ctr=ae", "Beach House", "12,000,000 LE", "Alexandria, Egypt"),
            FavoriteProperty(3,"https://www.propertyfinder.ae/property/12b02a5be8bc4c6fe9d687201869f6f4/416/272/MODE/4d9f5f/13634529-d796co.webp?ctr=ae", "Downtown Apartment", "5,000,000 LE", "Nasr City, Cairo"),
            FavoriteProperty(4,"https://www.propertyfinder.ae/property/45ebd0c4db2ee14ec4cb803368cad4be/416/272/MODE/0f8f4a/13624487-f1e5fo.webp?ctr=ae", "Penthouse Suite", "20,000,000 LE", "Zamalek, Cairo"),
            FavoriteProperty(5, "https://www.propertyfinder.ae/property/21c3952f973818c00bc9bc5104e65698/416/272/MODE/f2fdd1/13641259-b5db0o.webp?ctr=ae", "Modern Duplex", "8,500,000 LE", "New Cairo, Egypt"),
            FavoriteProperty(6, "https://www.propertyfinder.ae/property/d0aff6c54f4a6ef7128bf0331689084f/416/272/MODE/d2077f/13560948-4d9edo.webp?ctr=ae", "Cozy Studio", "3,000,000 LE", "6th of October, Egypt"),
            FavoriteProperty(7, "https://www.propertyfinder.ae/property/4e2c7f9388c8f119481e5a16c0dda203/416/272/MODE/0bdb19/13629095-e3203o.webp?ctr=ae", "Farmhouse Retreat", "7,500,000 LE", "El Fayoum, Egypt"),
            FavoriteProperty(8, "https://www.propertyfinder.ae/property/c169c31d851e2ffcb7c39cd8817315e9/416/272/MODE/5c15f2/13591645-d5ee3o.webp?ctr=ae", "Skyline Apartment", "10,000,000 LE", "Heliopolis, Cairo"),
            FavoriteProperty(9, "https://www.propertyfinder.ae/property/bdcdf5f3f661d802d57c6c998e851c0c/416/272/MODE/839bf0/13628887-450eao.webp?ctr=ae", "Family House", "6,000,000 LE", "Sheikh Zayed, Egypt"),
        )

    }

    fun getFavorites(): LiveData<List<FavoriteProperty>> = favoriteList
}