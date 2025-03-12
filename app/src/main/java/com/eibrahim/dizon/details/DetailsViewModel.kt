package com.eibrahim.dizon.details

import androidx.lifecycle.ViewModel
import com.eibrahim.dizon.R

class DetailsViewModel : ViewModel() {
    val price: String = "$1,000,000"
    val title: String = "Luxury Apartment"
    val description: String = "This is a beautiful and spacious apartment..."
    val isForSale: Boolean = true
    val imageUrl: String = "@drawable/ic_map" // Replace with actual URL
    val agentName: String = "Ibrahim Mohamed"
    val agentContact: String = "+1 123-456-7890"
    val agentImageUrl: String = "YOUR_AGENT_IMAGE_URL" // Replace with actual URL




    fun onCallClicked() {
        // Implement call functionality
    }

    fun onMessageClicked() {
        // Implement message functionality
    }

    fun onWhatsappClicked() {
        // Implement WhatsApp functionality
    }
}