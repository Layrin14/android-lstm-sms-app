package com.layrin.smsapp.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.SmsMessage
import androidx.preference.PreferenceManager
import com.layrin.smsapp.model.provider.SmsManager.Companion.KEY_RESUME_DATE
import com.layrin.smsapp.repository.ProviderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SmsReceiverService: Service() {

    private val context: Context = this

    private val job = CoroutineScope(Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent == null) {
            return super.onStartCommand(intent, flags, startId)
        }


        val contactManager = ProviderRepository(context).contactManager
        val bundle = intent.extras
        if (bundle != null) job.launch {
            val smsManager = ProviderRepository(context).smsManager
            val pdu = bundle["pdus"] as Array<*>
            val senders = hashMapOf<String, String>()

            for (item in pdu) {
                val currentMessage = SmsMessage.createFromPdu(item as ByteArray, bundle.getString("format"))
                val sender = currentMessage.displayOriginatingAddress
                val body = currentMessage.messageBody
                if (sender in senders) senders[sender] += body
                else senders[sender] = body
            }

            senders.forEach {
                it.apply {
                    val number = contactManager.getClean(key)
                    val data = smsManager.putMessage(
                        number,
                        value,
                        false
                    )
                }
            }
            smsManager.close()
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putLong(KEY_RESUME_DATE, System.currentTimeMillis()).apply()
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}