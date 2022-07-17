package com.layrin.smsapp.model

import androidx.paging.PagingSource
import androidx.room.*
import com.layrin.smsapp.common.BaseDao
import com.layrin.smsapp.model.contact.Contact
import com.layrin.smsapp.model.conversation.Conversation
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationAndContactDao: BaseDao<Contact> {

    @Transaction
    @Query("SELECT * FROM conversation WHERE number = :number")
    fun getConversationAndContact(number: String): List<ConversationAndContact>

    @Transaction
    @Query("SELECT * FROM conversation")
    fun getAllConversationAndContact(): List<ConversationAndContact>

    @Transaction
    @Query("SELECT name FROM contacts WHERE number = :number")
    fun getContactName(number: String): String?

    @Transaction
    @Query("SELECT * FROM conversation WHERE label = :label ORDER BY time DESC")
    fun getAllPagedConversation(label: Int): PagingSource<Int, Conversation>

    @Transaction
    @Query("UPDATE conversation SET read = 1 WHERE number = :number")
    fun updateRead(number: String)

    @Transaction
    @Query("SELECT * FROM contacts")
    fun getAllContacts(): Array<Contact>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContact(contact: Array<Contact>)

    @Transaction
    @Query("SELECT * FROM conversation WHERE number = :number")
    fun getConversation(number: String): Conversation?
}