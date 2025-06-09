package com.eibrahim.dizon.payment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import com.eibrahim.dizon.R
import com.eibrahim.dizon.addproperty.viewmodel.PropertyRepository
import com.eibrahim.dizon.chatbot.data.network.ChatLlamaStreamProcessor
import com.eibrahim.dizon.chatbot.data.network.HttpClient
import com.eibrahim.dizon.chatbot.domain.repositoryImpl.ChatRepositoryImpl
import com.eibrahim.dizon.chatbot.domain.usecase.GetChatResponseUseCase
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModelFactory
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.google.android.material.button.MaterialButton

class PaymentFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentFragment()
    }

    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModelFactory(
            PropertyRepository()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    private lateinit var continueButton: MaterialButton

    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continueButton = view.findViewById(R.id.continueButton)

        continueButton.setOnClickListener {

            sharedViewModel.propertyData.value?.let { data ->
                viewModel.addProperty(
                    data
                )
            }
        }

    }

}