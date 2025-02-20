package com.eibrahim.dizon.chatbot.view

import androidx.fragment.app.viewModels
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.viewModel.ChatbotViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.chatbot.model.Message
import com.eibrahim.dizon.chatbot.view.adapter.ChatAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatbotFragment : Fragment() {

    private val viewModel: ChatbotViewModel by viewModels()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var sendButton: Button

    // Maintains the conversation history.
    private val conversationHistory = mutableListOf<Message>()

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
        sendButton = view.findViewById(R.id.recordButton)
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        chatAdapter = ChatAdapter(conversationHistory)
        chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        bottomNavigationView.visibility = View.GONE
        // Send button click
        sendButton.setOnClickListener {
            val userMessage = inputEditText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                // Append the user's message
                val message = Message("user", userMessage)
                conversationHistory.add(message)
                chatAdapter.notifyItemInserted(conversationHistory.size - 1)
                chatRecyclerView.scrollToPosition(conversationHistory.size - 1)
                inputEditText.text.clear()

                // Send the message along with full conversation history to the backend
                viewModel.startChat(userMessage)
            }
        }

        // Observe the chat response LiveData
        viewModel.chatMessages.observe(viewLifecycleOwner, Observer { response ->
            val assistantMessage = Message("assistant", response)
            Log.d("test", response)
            conversationHistory.add(assistantMessage)
            chatAdapter.notifyItemInserted(conversationHistory.size - 1)
            chatRecyclerView.scrollToPosition(conversationHistory.size - 1)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomNavigationView.visibility = View.VISIBLE
    }
}