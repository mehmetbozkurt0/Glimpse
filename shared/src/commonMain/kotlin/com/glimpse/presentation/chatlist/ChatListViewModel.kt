package com.glimpse.presentation.chatlist

import androidx.lifecycle.viewModelScope
import com.glimpse.data.local.ChatEntity
import com.glimpse.domain.repository.ChatRepository
import com.glimpse.presentation.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatListViewModel(
    private val chatRepository: ChatRepository
) : BaseViewModel<ChatListState, ChatListEvent, ChatListEffect>() {

    override fun createInitialState(): ChatListState = ChatListState()

    init {
        observeChats()
    }

    private fun observeChats() {
        chatRepository.getAllChats()
            .onEach { chatList ->

                val displayList = chatList.ifEmpty {
                    listOf(
                        ChatEntity(
                            chatId = "test_agora_1",
                            name = "Agora Test Kişisi",
                            lastMessage = "Görüntülü aramayı test etmek için tıkla!",
                            updatedAt = 0L,
                            isGroup = 0L,
                        )
                    )
                }
                setState { copy(chats = displayList, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    override fun handleEvent(event: ChatListEvent) {
        when (event) {
            is ChatListEvent.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
            }
            is ChatListEvent.OnChatClicked -> {
                setEffect { ChatListEffect.NavigateToChat(event.chatId) }
            }
            ChatListEvent.RefreshChats -> {
            }
        }
    }
}