package com.eibrahim.dizon.payment.model

data class PaymentInfo(
    val cardHolderName: String,
    val cardNumber: String,
    val expiryMonth: String,
    val expiryYear: String,
    val securityCode: String,
    val cardType: String // "MasterCard" or "Visa"
)
