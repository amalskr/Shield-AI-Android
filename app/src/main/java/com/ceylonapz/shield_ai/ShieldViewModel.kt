package com.ceylonapz.shield_ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface UiState {
    data object Editing : UiState
    data object Checking : UiState
    data class Done(val text: String, val analysis: Analysis) : UiState
    data class Failed(val message: String) : UiState
}

class ShieldViewModel : ViewModel() {
    var input by mutableStateOf("")
        private set
    var replyLanguage by mutableStateOf("auto")
        private set
    var state by mutableStateOf<UiState>(UiState.Editing)
        private set

    private var job: Job? = null

    fun onInput(text: String) {
        input = text
        if (state is UiState.Failed) state = UiState.Editing
    }

    fun onReplyLanguage(code: String) {
        replyLanguage = code
    }

    /** Text shared from WhatsApp / SMS, or selected and sent via "Check with Shield-AI". */
    fun receiveShared(text: String) {
        input = text.trim()
        check()
    }

    fun check() {
        val text = input.trim()
        if (text.isEmpty()) {
            state = UiState.Failed("Paste a message first.")
            return
        }
        job?.cancel()
        state = UiState.Checking
        job = viewModelScope.launch {
            state = try {
                UiState.Done(text, ShieldApi.analyze(text, replyLanguage))
            } catch (e: IOException) {
                UiState.Failed("Shield-AI could not be reached. Check your internet connection and try again.")
            } catch (e: Exception) {
                UiState.Failed("The check did not finish. Try again in a few seconds.")
            }
        }
    }

    fun edit() {
        state = UiState.Editing
    }

    fun reset() {
        input = ""
        state = UiState.Editing
    }
}
