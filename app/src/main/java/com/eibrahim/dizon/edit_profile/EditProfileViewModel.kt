package com.eibrahim.dizon.edit_profile

import androidx.lifecycle.ViewModel

class EditProfileViewModel : ViewModel() {
    private val _cityList = listOf(
        "Cairo",
        "Alexandria",
        "Giza",
        "Shubra El-Kheima",
        "Port Said",
        "Suez",
        "Luxor",
        "Mansoura",
        "Tanta",
        "Asyut",
        "Ismailia",
        "Fayyum",
        "Zagazig",
        "Aswan",
        "Damietta",
        "Damanhur",
        "Al-Minya",
        "Beni Suef",
        "Qena",
        "Sohag"
    )

    fun getCityList(): List<String> {
        return _cityList
    }

    fun onCitySelected(city: String?) {
        // Handle selected city
    }

    fun loadData() {
        // Simulate data loading
    }

}
