package com.layrin.smsapp.repository

import androidx.paging.PagingSource
import com.layrin.smsapp.model.ConversationAndContact
import com.layrin.smsapp.model.ConversationAndMessage
import com.layrin.smsapp.model.contact.Contact
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.model.message.Message
import kotlinx.coroutines.flow.Flow

interface SmsRepository {

    suspend fun insert(obj: Conversation)

    suspend fun update(obj: Conversation)

    suspend fun delete(obj: Conversation)

    suspend fun insert(obj: Message)

    suspend fun update(obj: Message)

    suspend fun delete(obj: Message)

    suspend fun insert(obj: Contact)

    suspend fun update(obj: Contact)

    suspend fun delete(obj: Contact)

    /*
    ------------------------------
    Conversation and contact dao
    ------------------------------
     */

    suspend fun getConversationAndContact(number: String): List<ConversationAndContact>

    suspend fun getAllConversationAndAllContact(): List<ConversationAndContact>

    suspend fun getContactName(number: String): String?

    fun getAllPagedConversation(label: Int): PagingSource<Int, Conversation>

    suspend fun updateRead(number: String)

    suspend fun getAllContacts(): Array<Contact>

    suspend fun insertAllContact(contact: Array<Contact>)

    suspend fun getConversation(number: String): Conversation?



    /*
    ------------------------------
    Conversation and message dao
    ------------------------------
     */

    fun getAllConversationAndMessage(number: String): Flow<List<ConversationAndMessage>>

    fun getAllPaged(number: String): PagingSource<Int, Message>

    suspend fun insertAllMessages(messages: List<Message>)

    suspend fun getMessage(number: String): Message?

    suspend fun getLast(number: String): Message?

    suspend fun updateLabel(label: Int, number: String)
}