package com.layrin.smsapp.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.layrin.smsapp.model.contact.Contact
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.model.conversation.ConversationTypeConverter
import com.layrin.smsapp.model.message.Message

@Database(
    entities = [Conversation::class, Contact::class, Message::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ConversationTypeConverter::class)
abstract class SmsDb : RoomDatabase() {

    abstract fun conversationAndContactDao(): ConversationAndContactDao
    abstract fun conversationAndMessageDao(): ConversationAndMessageDao

}