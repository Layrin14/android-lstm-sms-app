package com.layrin.smsapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.repository.DaoRepository
import com.layrin.smsapp.repository.ProviderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    val providerRepository: ProviderRepository,
    private val daoRepository: DaoRepository,
    app: Application
) : AndroidViewModel(app) {

    var conversationData = MutableLiveData<HashMap<String, Pair<String?, String?>>>()

    private val categoryConversation = Array(3) {
        Pager(
            PagingConfig(
                pageSize = 12,
                initialLoadSize = 12,
                prefetchDistance = 12,
                maxSize = 180
            )
        ) {
            daoRepository.getAllPagedConversation(it)
        }.flow
    }

    fun updateRead(conversation: Conversation) {
        viewModelScope.launch {
            daoRepository.updateRead(conversation.number)
        }
    }

    fun updateLabel(conversation: Conversation, label: Int) {
        viewModelScope.launch {
            daoRepository.updateLabel(label, conversation.number)
        }
    }

    suspend fun getContactName(conversation: Conversation) = daoRepository.getContactName(conversation.number)

    suspend fun getLatestMessage(conversation: Conversation) = daoRepository.getLast(conversation.number)

    fun getCategoryConversation(): Array<Flow<PagingData<Conversation>>> = categoryConversation

    fun updateContactAsync() {
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = providerRepository.contactManager.getContactList()
            val cached = daoRepository.getAllContacts()
            daoRepository.insertAllContact(loaded)
            cached.forEach {
                if (it !in loaded) daoRepository.delete(it)
            }
        }
    }
}