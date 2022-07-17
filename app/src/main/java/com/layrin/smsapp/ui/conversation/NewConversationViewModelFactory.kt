package com.layrin.smsapp.ui.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.layrin.smsapp.model.ConversationAndContactDao
import com.layrin.smsapp.repository.DaoRepository

@Suppress("UNCHECKED_CAST")
class NewConversationViewModelFactory(
    private val daoRepository: DaoRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewConversationViewModel::class.java)) {
            return NewConversationViewModel(daoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}