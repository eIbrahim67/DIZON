package com.eibrahim.dizon.aboutUs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AboutUsViewModel : ViewModel() {

    private val _aboutUsContent = MutableStateFlow(
        """
            <b>Dizon</b> is a <i>next-generation digital platform</i> crafted to <b>redefine property management</b> and real estate search in Egypt. Whether you're looking to <i>buy, sell, or rent</i>, Dizon offers an <b>intelligent conversational assistant</b> that seamlessly understands both text and voice queries, delivering <i>instant, personalized property recommendations</i> tailored precisely to your preferences.<br/><br/>

            With its <b>powerful search capabilities</b> and <i>advanced filtering options</i>—by price, location, size, and more—Dizon’s <b>adaptive recommendation engine</b> continuously learns from your interactions to provide even more accurate and relevant suggestions, making your property journey <i>smarter, faster, and easier</i>.<br/><br/>

            Dizon also <b>streamlines document management</b> by intelligently extracting information from scanned documents and images, ensuring <i>secure storage</i> and easy retrieval. The <b>interactive user dashboard</b> keeps you informed with <i>real-time updates</i> at every stage of your transactions, while <b>secure e-payment solutions</b> and <i>role-based access control</i> safeguard the privacy and interests of all users—<b>buyers, sellers, agents, and property owners</b> alike.<br/><br/>

            By <i>aggregating listings</i> from diverse sources and offering smart tools to simplify every step of your real estate journey, Dizon delivers a <i>seamless, efficient, and future-ready</i> property experience designed to help you find your perfect home or investment with confidence.
        """.trimIndent()
    )
    val aboutUsContent: StateFlow<String> = _aboutUsContent

    fun loadAboutUsContent() {
        viewModelScope.launch {
            try {
                // Simulate fetching data (e.g., from a repository or remote source)
                // For now, the content is static as provided
                _aboutUsContent.value = aboutUsContent.value
            } catch (e: Exception) {
                Timber.e(e, "Error loading AboutUs content")
                // Fallback to default content or error message if needed
            }
        }
    }
}