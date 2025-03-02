package com.eibrahim.dizon.chatbot.presentation.view

import androidx.fragment.app.viewModels
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModel
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
    private val utils = UtilsFunctions

    private val conversationHistory = mutableListOf<ChatMessage>()

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        inputEditText = view.findViewById(R.id.inputEditText)
        sendButtonCard = view.findViewById(R.id.sendButtonCard)
        recordButton = view.findViewById(R.id.recordButton)
        uploadButton = view.findViewById(R.id.uploadButton)
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        chatAdapter = ChatAdapter(conversationHistory)
        chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        bottomNavigationView.visibility = View.GONE
        sendButtonCard.setOnClickListener {
            val userMessage = inputEditText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                val message = ChatMessage(userMessage, "user", true)
                conversationHistory.add(message)
                chatAdapter.notifyItemInserted(conversationHistory.size - 1)
                chatRecyclerView.scrollToPosition(conversationHistory.size - 1)
                inputEditText.text.clear()

                viewModel.startChat(userMessage)
            }
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    sendButtonCard.visibility = View.VISIBLE
                    uploadButton.visibility = View.GONE
                    recordButton.visibility = View.GONE
                } else {
                    sendButtonCard.visibility = View.GONE
                    uploadButton.visibility = View.VISIBLE
                    recordButton.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.chatMessages.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    //loading
                }

                is Response.Success -> {

                    val assistantMessage = response.data

                    try {
                        val rawContent = assistantMessage.content
                        // Split the content at the start of the "search_properties" key.
                        val splitIndex = rawContent.indexOf("\"search_properties\":")
                        if (splitIndex != -1) {
                            val messagePart = rawContent.substring(0, splitIndex).trim()
                            val searchPropertiesPart = rawContent.substring(splitIndex).trim()

                            // Optionally, remove any trailing commas or add missing braces.
                            // For instance, if messagePart looks like: "message": "Hello! How can I assist you today?"
                            // you might want to remove the key to get only the message text.
                            // Here's one way to extract just the message string:
                            val messageRegex = Regex("\"message\":\\s*\"(.*?)\"")
                            val matchResult = messageRegex.find(messagePart)
                            val messageString = matchResult?.groupValues?.get(1) ?: ""

                            // Now, for the searchPropertiesPart, you might need to add outer braces if missing.
//                            val fixedSearchProperties =
//                                if (!searchPropertiesPart.trim().startsWith("{")) {
//                                    "{ $searchPropertiesPart }"
//                                } else {
//                                    searchPropertiesPart
//                                }

                            //val searchProperties: SearchProperties = Gson().fromJson(fixedSearchProperties, SearchProperties::class.java)

                            Log.d("messageString", messageString)
                            Log.d("searchPropertiesPart", searchPropertiesPart)

                            conversationHistory.add(
                                ChatMessage(
                                    messageString,
                                    assistantMessage.role,
                                    assistantMessage.isFromUser
                                )
                            )
                            chatAdapter.notifyItemInserted(conversationHistory.size - 1)
                            chatRecyclerView.scrollToPosition(conversationHistory.size - 1)
                        } else {
                            Log.e("ParsingError", "Could not find search_properties key.")
                        }

                    } catch (e: Exception) {
                        Log.e("ParsingError", e.toString())
                    }
                }

                is Response.Failure -> {
                    utils.createFailureResponse(response, requireContext())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomNavigationView.visibility = View.VISIBLE
    }
}