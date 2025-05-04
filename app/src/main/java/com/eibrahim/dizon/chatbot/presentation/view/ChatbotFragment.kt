package com.eibrahim.dizon.chatbot.presentation.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.data.network.ChatLlamaStreamProcessor
import com.eibrahim.dizon.chatbot.data.network.HttpClient
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.chatbot.domain.repositoryImpl.ChatRepositoryImpl
import com.eibrahim.dizon.chatbot.domain.usecase.GetChatResponseUseCase
import com.eibrahim.dizon.chatbot.presentation.view.adapter.ChatAdapter
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModel
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModelFactory
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.core.utils.UtilsFunctions
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.eibrahim.dizon.search.data.Property
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class ChatbotFragment : Fragment() {

    private val viewModel: ChatbotViewModel by viewModels {
        val httpClient = HttpClient()
        val chatLlamaStreamProcessor = ChatLlamaStreamProcessor(httpClient)
        val repository = ChatRepositoryImpl(chatLlamaStreamProcessor)
        val getChatResponseUseCase = GetChatResponseUseCase(repository)
        ChatbotViewModelFactory(getChatResponseUseCase)
    }

    private val sharedViewModel: MainViewModel by activityViewModels()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var sendButtonCard: MaterialCardView
    private lateinit var recordButton: ImageView
    private lateinit var uploadButton: ImageView

    private val conversationHistory = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private var propertyList: List<Property> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize view references
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        inputEditText = view.findViewById(R.id.inputEditText)
        sendButtonCard = view.findViewById(R.id.sendButtonCard)
        recordButton = view.findViewById(R.id.recordButton)
        uploadButton = view.findViewById(R.id.uploadButton)
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)

        // Setup RecyclerView adapter with properties
        chatAdapter =
            ChatAdapter(conversationHistory, propertyList, goToAllResult = { goToAllResult() })

        chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Hide bottom navigation while chatting
        bottomNavigationView.visibility = View.GONE

        // Send button click listener
        sendButtonCard.setOnClickListener {
            val userMessage = inputEditText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                conversationHistory.add(
                    ChatMessage(
                        content = userMessage, role = "user", isFromUser = true
                    )
                )
                chatAdapter.notifyItemInserted(conversationHistory.size - 1)
                chatRecyclerView.scrollToPosition(conversationHistory.size - 1)
                inputEditText.text.clear()
                viewModel.startChat(userMessage)
            }
        }

        // Text change listener to toggle visibility of buttons
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    sendButtonCard.visibility = View.VISIBLE
                    recordButton.visibility = View.GONE
                } else {
                    sendButtonCard.visibility = View.GONE
                    recordButton.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) { /* no-op */
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) { /* no-op */
            }
        })
        viewModel.chatMessages.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                }

                is Response.Success -> {
                    response.data.search_properties.parameters.let { data ->
                        sharedViewModel.updateFilterParams(
                            city = data.location.city
                        )
                        sharedViewModel.updateFilterParams(
                            propertyType = data.property_type
                        )
                        sharedViewModel.updateFilterParams(
                            bathrooms = data.bathrooms.max
                        )
                        sharedViewModel.loadAllProperties()
                    }
                    val dummyImages = mutableListOf<String>()
                    conversationHistory.add(
                        ChatMessage(
                            content = response.data.message,
                            images = dummyImages
                        )
                    )
                    sharedViewModel.properties.observe(viewLifecycleOwner) { responseProp ->
                        if (responseProp != null) {
                            Log.d("SearchFragment", "Properties: ${responseProp.data}")
                            dummyImages.clear()
                            propertyList = responseProp.data.values.toList()
                            chatAdapter = ChatAdapter(
                                conversationHistory,
                                propertyList,
                                goToAllResult = { goToAllResult() })
                            chatRecyclerView.adapter = chatAdapter
                            for (x in responseProp.data.values) {
                                if (x.propertyImages.values.isNotEmpty()) {
                                    dummyImages.add(x.propertyImages.values[0])
                                }
                                Log.d("image", x.propertyImages.values.toString())
                            }
                            chatAdapter.updateLastBotMessageImages(dummyImages)
                        }
                    }
                    chatAdapter.notifyItemInserted(conversationHistory.size - 1)
                    chatRecyclerView.scrollToPosition(conversationHistory.size - 1)
                }

                is Response.Failure -> {
                    UtilsFunctions.createFailureResponse(response, requireContext())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomNavigationView.visibility = View.VISIBLE
    }

    private fun goToAllResult() {
        findNavController().navigate(R.id.action_chatbotFragment_to_propertyResultsFragment)
    }
}