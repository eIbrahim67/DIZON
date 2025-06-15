package com.eibrahim.dizon.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.eibrahim.dizon.R
import com.eibrahim.dizon.addproperty.viewmodel.PropertyRepository
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.eibrahim.dizon.payment.model.PaymentInfo
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentFragment()
    }

    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModelFactory(PropertyRepository())
    }

    private val sharedViewModel: MainViewModel by activityViewModels()
    private lateinit var progressBar: ProgressBar

    private lateinit var cardHolderNameEditText: EditText
    private lateinit var cardNumberEditText: EditText
    private lateinit var expiryMonthEditText: EditText
    private lateinit var expiryYearEditText: EditText
    private lateinit var securityCodeEditText: EditText
    private lateinit var masterCardImage: ImageView
    private lateinit var visaCardImage: ImageView
    private lateinit var continueButton: MaterialButton

    private var selectedCardType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        cardHolderNameEditText = view.findViewById(R.id.nameInput)
        cardNumberEditText = view.findViewById(R.id.cardNumberInput)
        expiryMonthEditText = view.findViewById(R.id.monthInput)
        expiryYearEditText = view.findViewById(R.id.yearInput)
        securityCodeEditText = view.findViewById(R.id.securityInput)
        masterCardImage = view.findViewById(R.id.master_card)
        visaCardImage = view.findViewById(R.id.visa)
        continueButton = view.findViewById(R.id.continueButton)
        progressBar = view.findViewById(R.id.progressBar)

        // Card type selection
        masterCardImage.setOnClickListener {
            selectedCardType = "MasterCard"
            Toast.makeText(requireContext(), "MasterCard selected", Toast.LENGTH_SHORT).show()
        }

        visaCardImage.setOnClickListener {
            selectedCardType = "Visa"
            Toast.makeText(requireContext(), "Visa selected", Toast.LENGTH_SHORT).show()
        }




    }

    private fun collectPaymentInfo(): PaymentInfo {
        return PaymentInfo(
            cardHolderName = cardHolderNameEditText.text.toString(),
            cardNumber = cardNumberEditText.text.toString(),
            expiryMonth = expiryMonthEditText.text.toString(),
            expiryYear = expiryYearEditText.text.toString(),
            securityCode = securityCodeEditText.text.toString(),
            cardType = selectedCardType ?: ""
        )
    }

    private fun isAllFieldsEntered(paymentInfo: PaymentInfo): Boolean {
        return paymentInfo.cardHolderName.isNotEmpty() &&
                paymentInfo.cardNumber.isNotEmpty() &&
                paymentInfo.expiryMonth.isNotEmpty() &&
                paymentInfo.expiryYear.isNotEmpty() &&
                paymentInfo.securityCode.isNotEmpty() &&
                paymentInfo.cardType.isNotEmpty()
    }

    private fun validateCard(paymentInfo: PaymentInfo): Boolean {
        val cardNumber = paymentInfo.cardNumber
        val cvv = paymentInfo.securityCode

        return when {
            // Case 1: Incomplete Card Data
            cardNumber.length < 13 || cvv.length < 3 -> {
                Toast.makeText(
                    requireContext(),
                    "Your card details appear incomplete. Please ensure the card number and security code are correctly filled.",
                    Toast.LENGTH_LONG
                ).show()
                false
            }

            // Case 3: Fake/Suspicious Data
            cardNumber.all { it == '0' } ||
                    paymentInfo.expiryMonth == "00" ||
                    paymentInfo.expiryYear == "99" -> {
                Toast.makeText(
                    requireContext(),
                    "The card details entered do not appear to be genuine. Please verify your information and try again.",
                    Toast.LENGTH_LONG
                ).show()
                false
            }

            // Case 2: Valid Data
            else -> {
//                Toast.makeText(
//                    requireContext(),
//                    "Card verified successfully. Proceeding with your payment.",
//                    Toast.LENGTH_LONG
//                ).show()
                true
            }
        }
    }
}
