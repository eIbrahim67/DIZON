package com.eibrahim.dizon.chatbot.presentation.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eibrahim.dizon.R
import com.eibrahim.dizon.chatbot.data.network.ChatLlamaStreamProcessor
import com.eibrahim.dizon.chatbot.data.network.HttpClient
import com.eibrahim.dizon.chatbot.domain.repositoryImpl.ChatRepositoryImpl
import com.eibrahim.dizon.chatbot.domain.usecase.GetChatResponseUseCase
import com.eibrahim.dizon.chatbot.presentation.view.adapter.ChatAdapter
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModel
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModelFactory
import com.eibrahim.dizon.main.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Fragment for the chatbot UI, handling user input, displaying conversation history,
 * and managing permissions for image and audio inputs.
 */
class ChatbotFragment : Fragment() {

    private val viewModel: ChatbotViewModel by viewModels {
        ChatbotViewModelFactory(
            GetChatResponseUseCase(
                ChatRepositoryImpl(
                    ChatLlamaStreamProcessor(HttpClient())
                )
            )
        )
    }
    private val sharedViewModel: MainViewModel by activityViewModels()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var sendButtonCard: MaterialCardView
    private lateinit var recordButton: ImageView
    private lateinit var uploadImageButton: ImageView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var backLayout: ImageView

    private var mediaRecorder: MediaRecorder? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { processImage(it) }
        }

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { processImage(it) }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            handlePermissionResult(granted, getStoragePermission())
        }

    private val recordAudioLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                toggleRecording()
            } else {
                showToast("Microphone permission denied")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_chatbot, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        bottomNavigationView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopRecording()
        bottomNavigationView.visibility = View.VISIBLE
    }

    /**
     * Initializes view references.
     */
    private fun initializeViews(view: View) {
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        inputEditText = view.findViewById(R.id.inputEditText)
        sendButtonCard = view.findViewById(R.id.sendButtonCard)
        recordButton = view.findViewById(R.id.recordButton)
        uploadImageButton = view.findViewById(R.id.uploadImageButton)
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        backLayout = view.findViewById(R.id.backLayout)
    }

    /**
     * Sets up the RecyclerView with the chat adapter.
     */
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter { navigateToPropertyResults() }
        chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    /**
     * Configures click and text change listeners for UI components.
     */
    private fun setupListeners() {
        uploadImageButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                photoPickerLauncher.launch(ActivityResultContracts.PickVisualMedia.ImageOnly)
                requestStoragePermission()
            } else {
                requestStoragePermission()
            }
        }

        sendButtonCard.setOnClickListener {
            val message = inputEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.startChat(message)
                inputEditText.text.clear()
            }
        }

        recordButton.setOnClickListener {
            requestAudioPermission()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateSendButtonVisibility(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        backLayout.setOnClickListener {

            findNavController().popBackStack()

        }
    }

    /**
     * Observes ViewModel LiveData for UI state updates.
     */
    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            chatAdapter.updateData(state.messages, state.properties)
            chatRecyclerView.scrollToPosition(state.messages.size - 1)
            sendButtonCard.visibility = if (state.isSendButtonVisible) View.VISIBLE else View.GONE
            recordButton.visibility = if (state.isSendButtonVisible) View.GONE else View.VISIBLE
            if (state.errorMessage != null) {
                showToast(state.errorMessage)
            }
            if (state.isRecording) {
                recordButton.setImageResource(R.drawable.ic_stop_recording) // Ensure you have this drawable
            } else {
                recordButton.setImageResource(R.drawable.icon_outline_mic) // Ensure you have this drawable
            }
        }

        viewModel.filterParams.observe(viewLifecycleOwner) { filterParams ->

                sharedViewModel.updateFilterParams(
                    city = filterParams.city,
                    propertyType = filterParams.propertyType,
                    bathrooms = filterParams.bathrooms,
                    bedrooms = filterParams.bedrooms
                )
                sharedViewModel.loadAllProperties()

        }

        sharedViewModel.properties.observe(viewLifecycleOwner){ props ->
            props?.data?.values?.let { viewModel.updateProperties(it) }
        }
    }

    /**
     * Processes an image by saving it to a temporary file and passing it to the ViewModel.
     */
    private fun processImage(uri: Uri) {
        lifecycleScope.launch {
            val file =
                File(requireContext().cacheDir, "temp_image_${System.currentTimeMillis()}.png")
            try {
                withContext(Dispatchers.IO) {
                    requireContext().contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    } ?: throw Exception("Cannot open image")
                }
                viewModel.processImage(file)
            } catch (e: Exception) {
                showToast("Error processing image: ${e.message}")
                file.delete()
            }
        }
    }

    /**
     * Requests storage permission based on Android version.
     */
    private fun requestStoragePermission() {
        val permission = getStoragePermission()
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED ->
                pickImage()

            shouldShowRequestPermissionRationale(permission) ->
                showPermissionRationaleDialog()

            else ->
                permissionLauncher.launch(permission)
        }
    }

    /**
     * Requests audio recording permission.
     */
    private fun requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            toggleRecording()
        }
    }

    /**
     * Determines the appropriate storage permission based on Android version.
     */
    private fun getStoragePermission(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    /**
     * Handles the result of a permission request.
     */
    private fun handlePermissionResult(granted: Boolean, permission: String) {
        if (granted) {
            pickImage()
        } else if (!shouldShowRequestPermissionRationale(permission)) {
            showSettingsDialog()
        } else {
            showToast("Storage permission denied")
        }
    }

    /**
     * Displays a dialog explaining the need for storage permission.
     */
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Storage Permission Required")
            .setMessage("This app needs access to your photos to upload images for OCR processing. Please grant the permission.")
            .setPositiveButton("Grant") { _, _ -> permissionLauncher.launch(getStoragePermission()) }
            .setNegativeButton("Cancel") { _, _ -> showToast("Permission denied") }
            .setCancelable(false)
            .show()
    }

    /**
     * Displays a dialog prompting the user to enable permissions in settings.
     */
    private fun showSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("Storage permission is needed to upload images. Please enable it in the app settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                })
            }
            .setNegativeButton("Cancel") { _, _ -> showToast("Permission denied") }
            .setCancelable(false)
            .show()
    }

    /**
     * Launches the image picker.
     */
    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }

    /**
     * Toggles audio recording state.
     */
    private fun toggleRecording() {
        if (viewModel.uiState.value?.isRecording == true) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    /**
     * Starts audio recording.
     */
    private fun startRecording() {
        val audioFile = File(requireContext().cacheDir, "audio_${System.currentTimeMillis()}.mp3")
        try {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            viewModel.setRecordingState(true)
            showToast("Recording started")
        } catch (e: Exception) {
            showToast("Recording failed: ${e.message}")
        }
    }

    /**
     * Stops audio recording and sends the file for transcription.
     */
    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            viewModel.setRecordingState(false)
            viewModel.processAudio(
                File(
                    requireContext().cacheDir,
                    "audio_${System.currentTimeMillis() - 1000}.mp3"
                )
            )
        } catch (e: Exception) {
            showToast("Recording failed: ${e.message}")
        }
    }

    /**
     * Navigates to the property results fragment.
     */
    private fun navigateToPropertyResults() {
        findNavController().navigate(R.id.action_chatbotFragment_to_propertyResultsFragment)
    }

    /**
     * Displays a toast message.
     */
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}