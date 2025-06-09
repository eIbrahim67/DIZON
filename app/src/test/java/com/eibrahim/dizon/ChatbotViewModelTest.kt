package com.eibrahim.dizon

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eibrahim.dizon.chatbot.domain.model.ChatMessage
import com.eibrahim.dizon.chatbot.domain.model.ChatbotViewModelConst
import com.eibrahim.dizon.chatbot.domain.model.SearchParameters
import com.eibrahim.dizon.chatbot.domain.model.SearchProperties
import com.eibrahim.dizon.chatbot.domain.usecase.GetChatResponseUseCase
import com.eibrahim.dizon.chatbot.presentation.viewModel.ChatbotViewModel
import com.eibrahim.dizon.core.response.ResponseEI
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import com.eibrahim.dizon.search.data.Property
import com.eibrahim.dizon.chatbot.domain.model.Location // Use the correct Location import
import com.google.gson.Gson
import com.eibrahim.dizon.chatbot.domain.model.ChatResponse
import com.eibrahim.dizon.chatbot.domain.model.Range
import com.eibrahim.dizon.chatbot.domain.model.RangeWithUnit
import com.eibrahim.dizon.chatbot.domain.model.RangeWithCurrency
import com.eibrahim.dizon.chatbot.domain.model.PropertyStatus
import com.eibrahim.dizon.chatbot.domain.model.Amenities
import com.eibrahim.dizon.core.response.FailureReason
import com.eibrahim.dizon.search.data.AmenitiesList
import com.eibrahim.dizon.search.data.ImageList
import com.eibrahim.dizon.search.data.OwnerInfo


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ChatbotViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetChatResponseUseCase
    private lateinit var viewModel: ChatbotViewModel
    private val gson = Gson()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        useCase = mockk()
        viewModel = ChatbotViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startChat with blank message does nothing`() = runTest {
        val before = viewModel.uiState.value?.messages
        viewModel.startChat("")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value?.messages).isEqualTo(before)
    }

    @Test
    fun `handle invalid JSON sets errorMessage`() = runTest {
        coEvery { useCase.execute(any()) } returns flowOf(
            ResponseEI.Success(
                ChatMessage(content = "not json", role = "assistant")
            )
        )

        viewModel.startChat("foo")
        advanceUntilIdle()

        val error = viewModel.uiState.value?.errorMessage
        assertThat(error).startsWith("Error parsing response")
    }

    @Test
    fun `successful chat response updates messages and filterParams`() = runTest {
        val mockMessageContent = "Here's a message about properties."
        val mockSearchParameters = SearchParameters(
            property_type = "apartment",
            location = Location(country = "USA", state = "NY", city = "New York", street_address = "12"),
            bedrooms = Range(min = 2, max = 3),
            bathrooms = Range(min = 1, max = 2),
            square_footage = RangeWithUnit(min = null, max = null, unit = ""), // Added default for new fields
            lot_size = RangeWithUnit(min = null, max = null, unit = ""), // Added default for new fields
            budget = RangeWithCurrency(min = 100000, max = 500000, currency = "USD"), // Updated budget
            transaction = "", // Added default for new fields
            property_status = PropertyStatus(condition = "", status = ""), // Added default for new fields
            amenities = Amenities(interior = emptyList(), exterior = emptyList(), accessibility = emptyList()) // Added default for new fields
        )
        val mockChatResponse = ChatResponse(
            message = mockMessageContent,
            search_properties = SearchProperties(action = "search", parameters = mockSearchParameters) // Added action
        )
        val jsonResponse = gson.toJson(mockChatResponse)

        coEvery { useCase.execute(any()) } returns flowOf(
            ResponseEI.Success(ChatMessage(content = jsonResponse, role = "assistant"))
        )

        viewModel.startChat("Find me an apartment in New York.")
        advanceUntilIdle()

        val messages = viewModel.uiState.value?.messages
        assertThat(messages).isNotNull()
        assertThat(messages?.size).isEqualTo(2) // User message + assistant message (system message is stripped for display)
        assertThat(messages?.last()?.content).isEqualTo(mockMessageContent)

        val filterParams = viewModel.filterParams.value
        assertThat(filterParams?.city).isEqualTo("New York")
        assertThat(filterParams?.propertyType).isEqualTo("apartment")
        assertThat(filterParams?.bedrooms).isEqualTo(2)
        assertThat(filterParams?.bathrooms).isEqualTo(1)
        assertThat(filterParams?.minPrice).isEqualTo(100000)
        assertThat(filterParams?.maxPrice).isEqualTo(500000)
        assertThat(viewModel.uiState.value?.errorMessage).isNull()
    }

    @Test
    fun `handle null or empty chat response content sets errorMessage`() = runTest {
        coEvery { useCase.execute(any()) } returns flowOf(
            ResponseEI.Success(ChatMessage(content = "", role = "assistant"))
        )

        viewModel.startChat("hello")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Invalid response: Content is empty")

        coEvery { useCase.execute(any()) } returns flowOf(
            ResponseEI.Success(ChatMessage(content = "", role = "assistant"))
        )

        viewModel.startChat("hello again")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Invalid response: Content is empty")
    }


//    @Test
//    fun `fetchChatResponse sets errorMessage on failure`() = runTest {
//        val errorMessage = "Network error"
//        coEvery { useCase.execute(any()) } returns flowOf(
//            ResponseEI.Failure(FailureReason.UnknownError("test error"))
//        )
//
//        viewModel.startChat("test message")
//        advanceUntilIdle()
//
//        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo(errorMessage)
//    }

//    @Test
//    fun `processImage with error sets errorMessage`() = runTest {
//        // Mocking ApiOcrClient requires PowerMock or directly mocking the static class,
//        // which is often complex. For unit tests, we'll simulate the error path
//        // by making the file processing itself fail, or by making the ViewModel's
//        // internal logic handle a hypothetical API error.
//        // As ApiOcrClient is an object, directly mocking its methods is difficult
//        // without a dependency injection framework or wrapper.
//        // For now, we'll test the error path that occurs *after* the file is passed to the ViewModel,
//        // which would typically be an API failure.
//
//        // We can't directly mock ApiOcrClient.ocrApiService without more advanced setup.
//        // Instead, we'll focus on how the ViewModel handles the *result* of that operation,
//        // assuming an error can be propagated.
//
//        // Simulate an error by having the internal processImage method fail
//        // This test will rely on a mock or a controlled environment if ApiOcrClient was injectable.
//        // For this scenario, we'll assume the error is caught within processImage itself.
//        val mockFile = File("nonexistent.png")
//        viewModel.processImage(mockFile) // This will throw an exception because the file doesn't exist
//        advanceUntilIdle()
//
//        assertThat(viewModel.uiState.value?.errorMessage).startsWith("Image processing failed:")
//    }


//    @Test
//    fun `processAudio with error sets errorMessage`() = runTest {
//        // Similar to processImage, directly mocking ApiVsrClient.vsrApiService
//        // is difficult. We'll simulate an error scenario where the internal
//        // call to transcribeAudio would fail.
//
//        // We can't directly mock ApiVsrClient.vsrApiService without more advanced setup.
//        // Instead, we'll focus on how the ViewModel handles the *result* of that operation,
//        // assuming an error can be propagated.
//
//        val mockFile = File("nonexistent.mp3")
//        viewModel.processAudio(mockFile) // This will throw an exception because the file doesn't exist
//        advanceUntilIdle()
//
//        assertThat(viewModel.uiState.value?.errorMessage).startsWith("Transcription failed after")
//    }

//    @Test
//    fun `updateProperties updates messages with images and properties`() = runTest {
//        viewModel.startChat("Test") // Adds user message and system prompt
//        advanceUntilIdle() // Process startChat and initial response (might be empty/error depending on mock)
//
//        // Manually add an assistant message for property update
//        val initialAssistantMessage = ChatMessage(content = "Properties coming soon", role = "assistant")
//        val chatResponse = ChatResponse(
//            message = "Properties coming soon",
//            search_properties = SearchProperties(action = "search", parameters = SearchParameters(
//                location = Location(country = "", state = "", city = "", street_address = ""),
//                property_type = "",
//                bedrooms = Range(min = null, max = null),
//                bathrooms = Range(min = null, max = null),
//                square_footage = RangeWithUnit(min = null, max = null, unit = ""),
//                lot_size = RangeWithUnit(min = null, max = null, unit = ""),
//                budget = RangeWithCurrency(min = null, max = null, currency = ""),
//                transaction = "",
//                property_status = PropertyStatus(condition = "", status = ""),
//                amenities = Amenities(interior = emptyList(), exterior = emptyList(), accessibility = emptyList())
//            ))
//        )
//        val jsonResponse = gson.toJson(chatResponse)
//        coEvery { useCase.execute(any()) } returns flowOf(
//            ResponseEI.Success(ChatMessage(content = jsonResponse, role = "assistant"))
//        )
//        viewModel.startChat("Show me properties") // Adds user message and an assistant message
//        advanceUntilIdle()
//
//        val mockProperty = Property(
//            propertyId = 1,
//            title = "Sample Property Title",
//            description = "This is a sample property description.",
//            price = 100000.0,
//            propertyType = "Apartment",
//            size = 120.5,
//            bedrooms = 3,
//            bathrooms = 2,
//            street = "123 Sample Street",
//            city = "Test City",
//            governate = "Test Governate",
//            listedAt = "2025-06-08T10:00:00Z",
//            propertyImages = ImageList(
//                id = "1",
//                values = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg")
//            ),
//            ownerInfo = OwnerInfo(
//                firstName = "John",
//                lastName = "Doe",
//                email = "john.doe@example.com",
//                phoneNumber = "+123456789"
//            ),
//            internalAmenities = AmenitiesList(
//                id = "2",
//                values = listOf("Air Conditioning", "Heater")
//            ),
//            externalAmenities = AmenitiesList(
//                id = "3",
//                values = listOf("Garden", "Garage")
//            ),
//            accessibilityAmenities = AmenitiesList(
//                id = "4",
//                values = listOf("Elevator", "Wheelchair Ramp")
//            )
//        )
//
//        val properties = listOf(mockProperty)
//
//        viewModel.updateProperties(properties)
//        advanceUntilIdle()
//
//        val messages = viewModel.uiState.value?.messages
//        assertThat(messages).isNotNull()
//        // We added a user message, a system message, and an assistant message.
//        // Then another user message and an assistant message for "Show me properties".
//        // The last assistant message should now have images.
//        assertThat(messages?.size).isEqualTo(4) // user, system, assistant, user, assistant
//        assertThat(messages?.last()?.images?.size).isEqualTo(1)
//        assertThat(messages?.last()?.images?.first()).isEqualTo("http://example.com/image1.jpg")
//        assertThat(viewModel.uiState.value?.properties).isEqualTo(properties)
//    }

    @Test
    fun `setRecordingState updates uiState correctly`() = runTest {
        assertThat(viewModel.uiState.value?.isRecording).isFalse()

        viewModel.setRecordingState(true)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value?.isRecording).isTrue()

        viewModel.setRecordingState(false)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value?.isRecording).isFalse()
    }

    @Test
    fun `updateSendButtonVisibility updates uiState correctly`() = runTest {
        assertThat(viewModel.uiState.value?.isSendButtonVisible).isFalse()

        viewModel.updateSendButtonVisibility("some text")
        advanceUntilIdle()
        assertThat(viewModel.uiState.value?.isSendButtonVisible).isTrue()

        viewModel.updateSendButtonVisibility("")
        advanceUntilIdle()
        assertThat(viewModel.uiState.value?.isSendButtonVisible).isFalse()
    }

    @Test
    fun `ensureSystemPrompt adds system message if not present`() = runTest {
        // Initial state should not have system message
        assertThat(viewModel.uiState.value?.messages).isEmpty()
        assertThat(viewModel.uiState.value?.messages?.any { it.role == "system" }).isFalse()

        // Call startChat which internally calls ensureSystemPrompt
        val mockMessageContent = "Hello from bot"
        val mockChatResponse = ChatResponse(
            message = mockMessageContent,
            search_properties = SearchProperties(action = "search", parameters = SearchParameters(
                location = Location(country = "", state = "", city = "", street_address = ""),
                property_type = "",
                bedrooms = Range(min = null, max = null),
                bathrooms = Range(min = null, max = null),
                square_footage = RangeWithUnit(min = null, max = null, unit = ""),
                lot_size = RangeWithUnit(min = null, max = null, unit = ""),
                budget = RangeWithCurrency(min = null, max = null, currency = ""),
                transaction = "",
                property_status = PropertyStatus(condition = "", status = ""),
                amenities = Amenities(interior = emptyList(), exterior = emptyList(), accessibility = emptyList())
            ))
        )
        val jsonResponse = gson.toJson(mockChatResponse)
        coEvery { useCase.execute(any()) } returns flowOf(
            ResponseEI.Success(ChatMessage(content = jsonResponse, role = "assistant"))
        )
        viewModel.startChat("user message")
        advanceUntilIdle()

        val messages = viewModel.uiState.value?.messages
        assertThat(messages).isNotNull()
        assertThat(messages?.size).isEqualTo(2) // User message + Assistant message (system message is stripped for display)
        // Check internal conversation history for system message
        val internalMessages = viewModel.javaClass.getDeclaredField("conversationHistory").apply { isAccessible = true }.get(viewModel) as List<ChatMessage>
        assertThat(internalMessages.any { it.role == "system" }).isTrue()
        assertThat(internalMessages.first { it.role == "system" }.content).isEqualTo(ChatbotViewModelConst.SYSTEM_PROMPT)
    }

    @Test
    fun `ensureSystemPrompt does not add system message if already present`() = runTest {
        // First, add a user message which will trigger ensureSystemPrompt
        val mockMessageContent = "Hello from bot"
        val mockChatResponse = ChatResponse(
            message = mockMessageContent,
            search_properties = SearchProperties(action = "search", parameters = SearchParameters(
                location = Location(country = "", state = "", city = "", street_address = ""),
                property_type = "",
                bedrooms = Range(min = null, max = null),
                bathrooms = Range(min = null, max = null),
                square_footage = RangeWithUnit(min = null, max = null, unit = ""),
                lot_size = RangeWithUnit(min = null, max = null, unit = ""),
                budget = RangeWithCurrency(min = null, max = null, currency = ""),
                transaction = "",
                property_status = PropertyStatus(condition = "", status = ""),
                amenities = Amenities(interior = emptyList(), exterior = emptyList(), accessibility = emptyList())
            ))
        )
        val jsonResponse = gson.toJson(mockChatResponse)
        coEvery { useCase.execute(any()) } returns flowOf(
            ResponseEI.Success(ChatMessage(content = jsonResponse, role = "assistant"))
        )
        viewModel.startChat("first message")
        advanceUntilIdle()

        // Get the internal conversation history
        val internalMessagesBefore = viewModel.javaClass.getDeclaredField("conversationHistory").apply { isAccessible = true }.get(viewModel) as List<ChatMessage>
        val systemMessageCountBefore = internalMessagesBefore.count { it.role == "system" }
        assertThat(systemMessageCountBefore).isEqualTo(1)

        // Call startChat again
        viewModel.startChat("second message")
        advanceUntilIdle()

        val internalMessagesAfter = viewModel.javaClass.getDeclaredField("conversationHistory").apply { isAccessible = true }.get(viewModel) as List<ChatMessage>
        val systemMessageCountAfter = internalMessagesAfter.count { it.role == "system" }
        assertThat(systemMessageCountAfter).isEqualTo(1) // Should still be 1
    }

//    @Test
//    fun `updateParamsFromResponse correctly updates filterParams`() = runTest {
//        val initialFilterParams = viewModel.filterParams.value
//        assertThat(initialFilterParams).isEqualTo(com.eibrahim.dizon.chatbot.presentation.viewModel.FilterParamsChatBot())
//
//        val newParams = SearchParameters(
//            property_type = "house",
//            location = Location(country = "UK", state = "England", city = "London", street_address = "12"),
//            bedrooms = Range(min = 3, max = 5),
//            bathrooms = Range(min = 2, max = 3),
//            square_footage = RangeWithUnit(min = 1500, max = 2000, unit = "sqft"),
//            lot_size = RangeWithUnit(min = 5000, max = 7000, unit = "sqft"),
//            budget = RangeWithCurrency(min = 200000, max = 800000, currency = "GBP"),
//            transaction = "sale",
//            property_status = PropertyStatus(condition = "new", status = "available"),
//            amenities = Amenities(interior = listOf("pool"), exterior = listOf("garden"), accessibility = emptyList())
//        )
//
//        viewModel.updateParamsFromResponse(newParams)
//        advanceUntilIdle()
//
//        val updatedFilterParams = viewModel.filterParams.value
//        assertThat(updatedFilterParams?.propertyType).isEqualTo("house")
//        assertThat(updatedFilterParams?.city).isEqualTo("London")
//        assertThat(updatedFilterParams?.bedrooms).isEqualTo(3)
//        assertThat(updatedFilterParams?.bathrooms).isEqualTo(2)
//        assertThat(updatedFilterParams?.minPrice).isEqualTo(200000)
//        assertThat(updatedFilterParams?.maxPrice).isEqualTo(800000)
//    }

    @Test
    fun `getDisplayMessages excludes system messages`() = runTest {
        val userMessage1 = ChatMessage(content = "Hi", role = "user", isFromUser = true)

        val chatResponse1 = ChatResponse(
            message = "Hello",
            search_properties = SearchProperties(action = "search", parameters = SearchParameters(
                location = Location(country = "", state = "", city = "", street_address = ""),
                property_type = "",
                bedrooms = Range(min = null, max = null),
                bathrooms = Range(min = null, max = null),
                square_footage = RangeWithUnit(min = null, max = null, unit = ""),
                lot_size = RangeWithUnit(min = null, max = null, unit = ""),
                budget = RangeWithCurrency(min = null, max = null, currency = ""),
                transaction = "",
                property_status = PropertyStatus(condition = "", status = ""),
                amenities = Amenities(interior = emptyList(), exterior = emptyList(), accessibility = emptyList())
            ))
        )
        val jsonResponse1 = gson.toJson(chatResponse1)

        coEvery { useCase.execute(any()) } returnsMany listOf(
            flowOf(ResponseEI.Success(ChatMessage(content = jsonResponse1, role = "assistant")))
        )

        viewModel.startChat(userMessage1.content!!)
        advanceUntilIdle()

        // Get the internal conversation history
        val internalMessages = viewModel.javaClass.getDeclaredField("conversationHistory").apply { isAccessible = true }.get(viewModel) as List<ChatMessage>
        assertThat(internalMessages.size).isEqualTo(3) // System, User, Assistant

        val displayMessages = viewModel.uiState.value?.messages
        assertThat(displayMessages).isNotNull()
        assertThat(displayMessages?.size).isEqualTo(2) // User, Assistant
        assertThat(displayMessages?.any { it.role == "system" }).isFalse()
    }
}
