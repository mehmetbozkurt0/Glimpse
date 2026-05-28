package com.glimpse.di

import com.glimpse.data.local.DatabaseDriverFactory
import com.glimpse.data.local.GlimpseDatabase
import com.glimpse.data.repository.AuthRepositoryImpl
import com.glimpse.data.repository.ChatRepositoryImpl
import com.glimpse.domain.repository.AuthRepository
import com.glimpse.domain.repository.ChatRepository
import com.glimpse.presentation.auth.AuthViewModel
import com.glimpse.presentation.chatlist.ChatListViewModel
import com.glimpse.presentation.chatroom.ChatRoomViewModel
import com.glimpse.presentation.videocall.VideoCallViewModel
import org.koin.dsl.module

val sharedModule = module {

    single { GlimpseDatabase(get<DatabaseDriverFactory>().createDriver()) }

    single<AuthRepository> { AuthRepositoryImpl() }
    single<ChatRepository> { ChatRepositoryImpl(get()) }

    factory { AuthViewModel(get()) }

    factory { ChatListViewModel(get()) }

    factory { ChatRoomViewModel(get()) }

    factory { VideoCallViewModel(get()) }
}