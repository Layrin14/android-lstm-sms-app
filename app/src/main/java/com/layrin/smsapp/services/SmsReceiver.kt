package com.layrin.smsapp.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SmsReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(
            Intent(context, SmsReceiverService::class.java).apply {
                action = intent?.action
                putExtra("pdus", intent?.extras?.get("pdus") as Array<*>)
                putExtra("format", intent.extras?.get("format") as String)
            }
        )
    }
}