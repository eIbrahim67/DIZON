package com.eibrahim.dizon.chatbot.view

import androidx.fragment.app.viewModels
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.viewModel.ChatbotViewModel
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
import com.eibrahim.dizon.chatbot.model.ChatMessage
import com.eibrahim.dizon.chatbot.view.adapter.ChatAdapter
import com.eibrahim.dizon.core.response.Response
import com.eibrahim.dizon.core.utils.UtilsFunctions
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatbotFragment : Fragment() {

    private val viewModel: ChatbotViewModel by viewModels()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: ImageView
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
        sendButton = view.findViewById(R.id.sendButton)
        recordButton = view.findViewById(R.id.recordButton)
        uploadButton = view.findViewById(R.id.uploadButton)
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        chatAdapter = ChatAdapter(conversationHistory)
        chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        bottomNavigationView.visibility = View.GONE
        sendButton.setOnClickListener {
            val userMessage = inputEditText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                val message = ChatMessage("user", userMessage)
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
                    sendButton.visibility = View.VISIBLE
                    uploadButton.visibility = View.GONE
                    recordButton.visibility = View.GONE
                } else {
                    sendButton.visibility = View.GONE
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

                }
                is Response.Success -> {
                    val assistantMessage = ChatMessage("assistant", response.data)
                    Log.d("test", response.data)
                    conversationHistory.add(assistantMessage)
                    chatAdapter.notifyItemInserted(conversationHistory.size - 1)
                    chatRecyclerView.scrollToPosition(conversationHistory.size - 1)
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