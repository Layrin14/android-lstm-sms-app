package com.layrin.smsapp.model

import androidx.room.Embedded
import androidx.room.Relation
import com.layrin.smsapp.model.contact.Contact
import com.layrin.smsapp.model.conversation.Conversation

// one-to-one relation, one contact can only have one conversation
data class ConversationAndContact(
    @Embedded val conversation: Conversation,
    @Relation(
        parentColumn = "number",
        entityColumn = "number"
    ) val contact: Contact?
)