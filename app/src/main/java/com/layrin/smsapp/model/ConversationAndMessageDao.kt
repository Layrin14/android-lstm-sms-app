package com.layrin.smsapp.model

import androidx.paging.PagingSource
import androidx.room.*
import com.layrin.smsapp.common.BaseDao
import com.layrin.smsapp.model.message.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationAndMessageDao: BaseDao<Message> {

    @Transaction
    @Query("SELECT * FROM conversation WHERE number = :number")
    fun getAllConversationAndMessage(number: String): Flow<List<ConversationAndMessage>>

    @Transaction
    @Query("SELECT * FROM messages WHERE number = :number ORDER BY time DESC")
    fun getAllPaged(number: String): PagingSource<Int, Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessages(messages: List<Message>)

    @Transaction
    @Query("SELECT * FROM messages WHERE number = :number")
    suspend fun getMessage(number: String): Message?

    @Transaction
    @Query("SELECT * FROM messages WHERE number = :number ORDER BY time DESC LIMIT 1")
    fun getLast(number: String): Message?

    @Transaction
    @Query("UPDATE conversation SET label = :label WHERE number = :number")
    suspend fun updateLabel(label: Int, number: String)
}