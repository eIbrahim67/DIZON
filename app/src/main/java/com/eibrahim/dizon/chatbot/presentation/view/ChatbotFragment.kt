package com.eibrahim.dizon.chatbot.presentation.view

import androidx.fragment.app.viewModels
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.chatbot.data.network.ChatLlamaStreamProcessor
import com.eibrahim.dizon.chatbot.data.network.HttpClient
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.chatbot.domain.repository.ChatRepositoryImpl
import com.eibrahim.dizon.chatbot.domain.usecase.GetChatResponseUseCase
import com.eibrahim.dizon.chatbot.presentation.view.adapter.ChatAdapter
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModelFactory
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.core.utils.UtilsFunctions
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

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var sendButtonCard: MaterialCardView
    private lateinit var recordButton: ImageView
    private lateinit var uploadButton: ImageView

    private val conversationHistory = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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


        conversationHistory.add(
            ChatMessage(
                content = "Hi",
                role = "user",
                isFromUser = true,
                images = null
            ),
        )
        conversationHistory.add(
            ChatMessage(
                content = "Hello, How can I assist you?",
                role = "bot",
                isFromUser = false,
                images = null
            ),
        )
        // Setup RecyclerView adapter.
        chatAdapter = ChatAdapter(conversationHistory)

        chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


        // Hide bottom navigation while chatting.
        bottomNavigationView.visibility = View.GONE

        // Send button click listener.
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

        // Text change listener to toggle visibility of buttons.
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

        // Observe chat responses from the ViewModel.
        viewModel.chatMessages.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    // Optionally show a loading indicator here.
                }

                is Response.Success -> {
                    var dummyImages: List<String>? = null
                    // Add the bot's response to the conversation history.
                    // Check for additional properties if needed.
                    if (response.data.search_properties.parameters.displaySearchResults) {
                        dummyImages = listOf(
                            "https://www.propertyfinder.ae/property/aabc8ff45bd7d606b615a23f3adf928e/416/272/MODE/35a74d/13614520-ffa59o.webp?ctr=ae",
                            "https://www.propertyfinder.ae/property/d5596f824e7b2fe3d8e4820fb91ec05e/416/272/MODE/0df2db/13635501-e8d76o.webp?ctr=ae",
                            "https://www.propertyfinder.ae/property/12b02a5be8bc4c6fe9d687201869f6f4/416/272/MODE/4d9f5f/13634529-d796co.webp?ctr=ae",
                            "https://www.propertyfinder.ae/property/45ebd0c4db2ee14ec4cb803368cad4be/416/272/MODE/0f8f4a/13624487-f1e5fo.webp?ctr=ae"
                        )
                    }
                    conversationHistory.add(
                        ChatMessage(
                            content = response.data.message, // Optionally leave empty or add a caption.
                            images = dummyImages
                        )
                    )

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
        // Restore bottom navigation visibility.
        bottomNavigationView.visibility = View.VISIBLE
        // Nullify view references if needed to prevent memory leaks.
        // For example, if using view binding, set binding = null here.
    }
}
