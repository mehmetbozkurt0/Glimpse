package com.glimpse.presentation.chatroom

import androidx.lifecycle.viewModelScope
import com.glimpse.domain.repository.ChatRepository
import com.glimpse.presentation.mvi.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatRoomViewModel(
    private val chatRepository: ChatRepository
) : BaseViewModel<ChatRoomState, ChatRoomEvent, ChatRoomEffect>() {

    override fun createInitialState(): ChatRoomState = ChatRoomState()

    private var observeJob: Job? = null

    fun initChat(chatId: String) {
        observeJob?.cancel()

        observeJob = chatRepository.getMessagesByChat(chatId)
            .onEach { messageList ->
                setState { copy(messages = messageList, isLoading = false) }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            chatRepository.connectAndListen()
        }
    }

    override fun handleEvent(event: ChatRoomEvent) {
        when (event) {
            is ChatRoomEvent.OnMessageChanged -> {
                setState { copy(currentMessage = event.message) }
            }
            is ChatRoomEvent.SendMessage -> {
                val content = uiState.value.currentMessage
                if (content.isNotBlank()) {
                    viewModelScope.launch {
                        chatRepository.sendMessage(chatId = event.chatId, content = content)
                        setState { copy(currentMessage = "") }
                    }
                }
            }
            ChatRoomEvent.OnBackClicked -> {
                setEffect { ChatRoomEffect.NavigateBack }
            }
            is ChatRoomEvent.OnVideoCallClicked -> {
                setEffect { ChatRoomEffect.NavigateToVideoCall(event.chatId) }
            }
        }
    }
}