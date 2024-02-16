package com.espressodev.gptmap.api.chatgpt.module

import com.espressodev.gptmap.api.chatgpt.ChatgptService
import com.espressodev.gptmap.api.chatgpt.impl.ChatgptServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideChatgptService(impl: ChatgptServiceImpl): ChatgptService
}
