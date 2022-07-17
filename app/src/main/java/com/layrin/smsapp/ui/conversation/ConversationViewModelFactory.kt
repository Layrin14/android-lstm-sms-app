package com.layrin.smsapp.ui.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.layrin.smsapp.repository.DaoRepository

@Suppress("UNCHECKED_CAST")
class ConversationViewModelFactory(
    private val daoRepository: DaoRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationViewModel::class.java)) {
            return ConversationViewModel(daoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}