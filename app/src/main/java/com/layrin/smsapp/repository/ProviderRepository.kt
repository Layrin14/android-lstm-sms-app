package com.layrin.smsapp.repository

import android.app.Application
import android.content.Context
import com.layrin.smsapp.SmsApplication
import com.layrin.smsapp.model.ConversationAndContactDao
import com.layrin.smsapp.model.ConversationAndMessageDao
import com.layrin.smsapp.model.provider.ContactManager
import com.layrin.smsapp.model.provider.SmsManager

class ProviderRepository(context: Context) {
    private val conversationAndMessageDao: ConversationAndMessageDao by lazy {
        SmsApplication.database.conversationAndMessageDao()
    }

    private val conversationAndContactDao: ConversationAndContactDao by lazy {
        SmsApplication.database.conversationAndContactDao()
    }

    val contactManager = ContactManager(context)

    val smsManager = SmsManager(context, conversationAndContactDao, conversationAndMessageDao)
}