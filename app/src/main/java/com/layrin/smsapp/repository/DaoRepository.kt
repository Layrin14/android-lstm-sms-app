package com.layrin.smsapp.repository

import androidx.paging.PagingSource
import com.layrin.smsapp.SmsApplication
import com.layrin.smsapp.model.ConversationAndContact
import com.layrin.smsapp.model.ConversationAndContactDao
import com.layrin.smsapp.model.ConversationAndMessage
import com.layrin.smsapp.model.ConversationAndMessageDao
import com.layrin.smsapp.model.contact.Contact
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.model.message.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DaoRepository: SmsRepository {

    private val conversationAndMessageDao: ConversationAndMessageDao by lazy {
        SmsApplication.database.conversationAndMessageDao()
    }

    private val conversationAndContactDao: ConversationAndContactDao by lazy {
        SmsApplication.database.conversationAndContactDao()
    }

    private val allConversationAndContact by lazy {
        conversationAndContactDao.getAllConversationAndContact()
    }

    private val allContact by lazy {
        conversationAndContactDao.getAllContacts()
    }

    override suspend fun insert(obj: Conversation) {
        conversationAndContactDao.insert(obj)
    }

    override suspend fun update(obj: Conversation) {
        conversationAndContactDao.update(obj)
    }

    override suspend fun delete(obj: Conversation) {
        conversationAndContactDao.delete(obj)
    }

    override suspend fun insert(obj: Message) {
        conversationAndMessageDao.insert(obj)
    }

    override suspend fun update(obj: Message) {
        conversationAndMessageDao.update(obj)
    }

    override suspend fun delete(obj: Message) {
        conversationAndMessageDao.delete(obj)
    }

    override suspend fun insert(obj: Contact) {
        conversationAndContactDao.insert(obj)
    }

    override suspend fun update(obj: Contact) {
        conversationAndContactDao.update(obj)
    }

    override suspend fun delete(obj: Contact) {
        conversationAndContactDao.delete(obj)
    }

    override suspend fun getConversationAndContact(number: String): List<ConversationAndContact> =
        conversationAndContactDao.getConversationAndContact(number)

    override suspend fun getAllConversationAndAllContact(): List<ConversationAndContact> = allConversationAndContact

    override suspend fun getContactName(number: String): String? =
        conversationAndContactDao.getContactName(number)

    override fun getAllPagedConversation(label: Int): PagingSource<Int, Conversation> =
        conversationAndContactDao.getAllPagedConversation(label)

    override suspend fun updateRead(number: String) {
        withContext(Dispatchers.IO) {
            conversationAndContactDao.updateRead(number)
        }
    }

    override suspend fun getAllContacts(): Array<Contact> = allContact

    override suspend fun insertAllContact(contact: Array<Contact>) {
        withContext(Dispatchers.IO) {
            conversationAndContactDao.insertAllContact(contact)
        }
    }

    override suspend fun getConversation(number: String): Conversation? =
        conversationAndContactDao.getConversation(number)

    override fun getAllConversationAndMessage(number: String): Flow<List<ConversationAndMessage>> =
        conversationAndMessageDao.getAllConversationAndMessage(number)

    override fun getAllPaged(number: String): PagingSource<Int, Message> =
        conversationAndMessageDao.getAllPaged(number)

    override suspend fun insertAllMessages(messages: List<Message>) {
        withContext(Dispatchers.IO) {
            conversationAndMessageDao.insertAllMessages(messages)
        }
    }

    override suspend fun getMessage(number: String): Message? =
        conversationAndMessageDao.getMessage(number)

    override suspend fun getLast(number: String): Message? =
        conversationAndMessageDao.getLast(number)

    override suspend fun updateLabel(label: Int, number: String) =
        withContext(Dispatchers.IO) {
            conversationAndMessageDao.updateLabel(label, number)
        }
}