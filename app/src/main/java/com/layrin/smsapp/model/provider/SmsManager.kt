package com.layrin.smsapp.model.provider

import android.content.Context
import android.net.Uri
import androidx.preference.PreferenceManager
import com.layrin.smsapp.common.saveSms
import com.layrin.smsapp.model.ConversationAndContactDao
import com.layrin.smsapp.model.ConversationAndMessageDao
import com.layrin.smsapp.model.conversation.Conversation
import com.layrin.smsapp.model.message.Message
import com.layrin.smsapp.model.model.Model
import com.layrin.smsapp.ui.conversation.MessageRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SmsManager(
    private val context: Context,
    private val conversationDao: ConversationAndContactDao,
    private val messageDao: ConversationAndMessageDao,
) {

    companion object {
        const val KEY_RESUME_DATE = "last_date"
        const val KEY_RESUME_INDEX = "last_index"
        const val KEY_DONE_COUNT = "done_count"
        const val KEY_TIME_TAKEN = "time_taken"
    }

    private val messages = HashMap<String, ArrayList<Message>>()
    private val messageProbability = HashMap<String, Array<Float>>()
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val contactManager = ContactManager(context)
    private val job = CoroutineScope(Dispatchers.IO)

    private lateinit var model: Model
    private lateinit var contactHashMap: HashMap<String, String>

    private var total = 0
    private var index = 0
    private var done = 0
    private var timeTaken = 0L
    private var startTime = 0L
    private var isWorkingAfterInit = false

    private fun initLate() {
        if (!::contactHashMap.isInitialized || contactHashMap.isEmpty()) {
            contactHashMap = contactManager.getContactHashMap()
            model = Model(context)
        }
    }

    var onStatusChangeListener: (Int) -> Unit = {}
    var onProgressListener: (Float) -> Unit = {}
    var onEtaChangeListener: (Long) -> Unit = {}

    private fun finish() {
        onStatusChangeListener(2)

        if (!isWorkingAfterInit) model.close()
        messageProbability.clear()
        contactHashMap.clear()

        preferences.edit()
            .remove(KEY_RESUME_INDEX)
            .putLong(KEY_RESUME_DATE, System.currentTimeMillis())
            .apply()

        isWorkingAfterInit = false
        job.cancel()
    }

    private fun updateProgress(size: Int) {
        done += size
        timeTaken = (System.currentTimeMillis() - startTime)
        val per = done * 100f / total
        val eta = (System.currentTimeMillis() - startTime) * (100 / per - 1)
        onProgressListener(per)
        onEtaChangeListener(eta.toLong())
    }

    fun getMessages() {
        initLate()

        val lastDate = preferences.getLong(KEY_RESUME_DATE, 0).toString()

        context.contentResolver.query(
            Uri.parse("content://sms/"),
            null,
            "date" + ">?",
            arrayOf(lastDate),
            "date ASC"
        )?.apply {
            if (moveToFirst()) {
                val nameId = getColumnIndex("address")
                val messageId = getColumnIndex("body")
                val dateId = getColumnIndex("date")
                val typeId = getColumnIndex("type")

                do {
                    val name = getString(nameId)
                    val messageBody = getString(messageId)
                    val id = getString(getColumnIndex("_id")).toInt()

                    if (name != null && !messageBody.isNullOrEmpty()) {
                        val number = contactManager.getClean(name)
                        val message = Message(messageBody,
                            getString(dateId).toLong(),
                            id,
                            number,
                            getString(typeId).toInt())

                        if (messages.containsKey(number)) messages[number]?.add(message)
                        else messages[number] = arrayListOf(message)
                    }
                } while (moveToNext())
            }
            close()
        }
    }

    fun getLabels() {
        index = preferences.getInt(KEY_RESUME_INDEX, 0)

        for ((_, msg) in messages) total += msg.size

        val messageArray = messages.entries.toTypedArray()
        val totalNumber = messageArray.size

        val saveCoroutine = CoroutineScope(Dispatchers.IO)
        startTime = System.currentTimeMillis() - preferences.getLong(KEY_TIME_TAKEN, 0)

        for (idx in index until totalNumber) {
            val number = messageArray[idx].component1()
            val msg = messageArray[idx].component2()
            var label: Int

            if (contactHashMap.containsKey(number)) label = 0
            else {
                val probability = model.getPredictions(msg)
                messageProbability[number] = probability
                label = probability.toList().indexOf(probability.maxOrNull())
            }
            saveCoroutine.launch {
                saveMessages(idx, number, messages[number]!!, label)
            }.start()
            updateProgress(msg.size)
        }
        saveCoroutine.cancel()
        finish()
    }

    private fun deletePrevious(number: String): Conversation? {
        var conversation: Conversation? = null
        for (i in 0..2) {
            job.launch {
                val data = conversationDao.getConversation(number)
                if (data != null) {
                    conversation = data
                    conversationDao.delete(data)
                }
            }
        }
        return conversation
    }

    private fun saveMessages(idx: Int, number: String, messages: ArrayList<Message>, label: Int) {
        if (number.isBlank()) return

        val conversation = deletePrevious(number)
        if (conversation != null) {
            conversation.apply {
                read = !isWorkingAfterInit
                time = messages.last().time
                latestMessage = messages.last().text
            }
            job.launch {
                conversationDao.insert(conversation)
            }
        } else {
            val conv = Conversation(
                number,
                messages.last().time,
                label,
                messageProbability[number] ?: Array(3) { if (it == 0) 1f else 0f },
                !isWorkingAfterInit,
                messages.last().text
            )
            job.launch {
                conversationDao.insert(conv)
            }
        }

        job.launch {
            messageDao.insertAllMessages(messages.toList())
        }

        preferences.edit()
            .putInt(KEY_RESUME_INDEX, idx + 1)
            .putInt(KEY_DONE_COUNT, done)
            .putLong(KEY_TIME_TAKEN, timeTaken)
            .apply()
    }

    fun putMessage(number: String, body: String, read: Boolean): Pair<Message, Conversation> {
        initLate()

        val rawNumber = contactManager.getClean(number)
        val message = Message(body,
            System.currentTimeMillis(),
            type = MessageRecyclerViewAdapter.ITEM_VIEW_TYPE_RECEIVE,
            number = rawNumber)

        var conversation = deletePrevious(rawNumber)

        var probability: Array<Float>? = null
        val prediction = if (contactHashMap.containsKey(rawNumber)) 0
        else {
            probability = model.getPrediction(message)
            if (conversation != null)
                for (i in 0..2) probability[i] += conversation.probability[i]
            var label = probability.toList().indexOf(probability.maxOrNull())
            if (label == 0 && rawNumber.first().isLetter()) {
                probability.clone().apply {
                    this[0] = 0f
                    label = probability.toList().indexOf(probability.max())
                }
            }
            label
        }

        conversation = conversation?.apply {
            this.read = read
            time = message.time
            label = prediction
            this.probability = probability ?: this.probability
        } ?: Conversation(
            rawNumber,
            read = false,
            time = message.time,
            label = prediction,
            latestMessage = message.text
        )

        message.id =
            context.saveSms(number, body, MessageRecyclerViewAdapter.ITEM_VIEW_TYPE_RECEIVE)
        job.launch {
            messageDao.insert(message)
            messageDao.insert(conversation)
        }

        return message to conversation
    }

    fun updateAsync() {
        if (isWorkingAfterInit) return
        isWorkingAfterInit = true
        job.launch {
            getMessages()
            getLabels()
        }.start()
    }

    fun updateSync() {
        if (isWorkingAfterInit) return
        isWorkingAfterInit = true
        getMessages()
        getLabels()
    }

    fun close() = model.close()
}