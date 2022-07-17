package com.layrin.smsapp.model

import androidx.room.Embedded
import androidx.room.Relation
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.model.message.Message

// one-to-many relationship, one conversation can have many messages
data class ConversationAndMessage(
    @Embedded val conversation: Conversation,
    @Relation(
        parentColumn = "number",
        entityColumn = "number"
    ) val message: List<Message>
)