package com.layrin.smsapp.ui.conversation

import androidx.lifecycle.ViewModel
import com.layrin.smsapp.model.ConversationAndContactDao
import com.layrin.smsapp.repository.DaoRepository

class NewConversationViewModel(
    private val daoRepository: DaoRepository
) : ViewModel() {

    suspend fun getContacts() = daoRepository.getAllContacts()
}