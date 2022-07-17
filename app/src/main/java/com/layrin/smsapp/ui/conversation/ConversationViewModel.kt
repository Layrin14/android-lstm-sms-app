package com.layrin.smsapp.ui.conversation

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.layrin.smsapp.model.ConversationAndMessage
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.model.message.Message
import com.layrin.smsapp.repository.DaoRepository
import kotlinx.coroutines.flow.Flow

class ConversationViewModel(
    private val daoRepository: DaoRepository
) : ViewModel() {

    lateinit var messages: List<Message>

    lateinit var conversation: Conversation

    private lateinit var pagedMessage: Flow<PagingData<Message>>

    val pagingData get() = pagedMessage

    suspend fun getMessage(conversation: Conversation) = daoRepository.getMessage(conversation.number)

    suspend fun getContactName() = daoRepository.getContactName(conversation.number)

    fun getAllMessages(number: String): Flow<List<ConversationAndMessage>> =
        daoRepository.getAllConversationAndMessage(number)

    fun init(conv: Conversation) {
        if (::conversation.isInitialized) return

        conversation = conv

        pagedMessage = Pager(
            PagingConfig(
                pageSize = 15,
                initialLoadSize = 15,
                prefetchDistance = 60,
                maxSize = 180
            )
        ) {
            daoRepository.getAllPaged(conversation.number)
        }.flow
    }

    suspend fun insertAll(message: Message) {
        daoRepository.insert(message)
    }

}