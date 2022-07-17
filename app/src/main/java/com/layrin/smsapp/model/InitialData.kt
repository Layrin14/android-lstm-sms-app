package com.layrin.smsapp.model

import com.layrin.smsapp.model.contact.Contact
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.model.message.Message

class InitialData {
    companion object{
        val contacts = listOf(
            Contact("0823", "John", 0),
            Contact("0821", "Dumb", 1),
            Contact("0828", "Kampret", 2),
            Contact("0829", "Kampret_2", 3),
            Contact("0820", "Kampret_3", 4),
        )

        val conversations = listOf(
            Conversation("0823", 10, 0, arrayOf(1F, 0F, 0F), false),
            Conversation("0821", 10, 1, arrayOf(0F, 1F, 0F), false),
            Conversation("0828", 10, 2, arrayOf(0F, 0F, 1F)),
//            Conversation("0829", 11, 2, arrayOf(0F, 0F, 1F)),
//            Conversation("0820", 12, 1, arrayOf(0F, 1F, 0F)),
        )

        val messages = listOf(
            Message("Hi!", 9, 0, "0823", 0, true),
            Message("Hello!", 10, 1, "0823", 1, true),
            Message("How are you?", 11, 2, "0823", 0, true),
            Message("I'm fine, f**k u", 12, 3, "0823", 1, true),
            Message("Hi, 2!", 9, 4, "0821", 0, true),
            Message("Hello!, 2", 10, 5, "0821", 1, true),
            Message("How are you?, 2", 11, 6, "0821", 0, true),
            Message("I'm fine, f**k u, 2", 12, 7, "0821", 1, true),
            Message("Hi!, 3", 9, 8, "0828", 0, true),
            Message("Hello!, 3", 10, 9, "0828", 1, true),
            Message("How are you?, 3", 11, 10, "0828", 0, true),
            Message("I'm fine, f**k u, 3", 12, 11, "0828", 1, true)
        )
    }
}