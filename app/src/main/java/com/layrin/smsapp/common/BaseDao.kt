package com.layrin.smsapp.common

import androidx.room.*
import com.layrin.smsapp.model.conversation.Conversation

interface BaseDao<U> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg obj: Conversation)

    @Update
    suspend fun update(obj: Conversation)

    @Delete
    suspend fun delete(obj: Conversation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: U)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg obj: U)

    @Update
    suspend fun update(obj: U)

    @Delete
    suspend fun delete(obj: U)
}