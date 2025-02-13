package com.klipy.demoapp.presentation.di

import com.klipy.demoapp.presentation.algorithm.MasonryMeasurementsCalculator
import com.klipy.demoapp.presentation.features.conversation.ConversationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module{
    viewModel { (conversationId: String?) ->
        ConversationViewModel(conversationId, get(), get())
    }

    single { MasonryMeasurementsCalculator }
}