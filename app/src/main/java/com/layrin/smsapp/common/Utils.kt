package com.layrin.smsapp.common

import android.app.role.RoleManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Telephony
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity


fun Context.deleteSms(id: Int) {
    if (packageName == Telephony.Sms.getDefaultSmsPackage(this)) {
        contentResolver.delete(
            Uri.parse("content://sms/$id"),
            null, null
        )
    }
}

fun Context.saveSms(number: String, message: String, type: Int): Int? {
    if (packageName != Telephony.Sms.getDefaultSmsPackage(this)) return null
    val date = System.currentTimeMillis()
    val values = ContentValues().apply {
        put("address", number)
        put("body", message)
        put("read", 1)
        put("date", date)
        put("type", type)
    }
    val uri = contentResolver.insert(Telephony.Sms.CONTENT_URI, values)
    return uri?.lastPathSegment?.toInt()
}

fun AppCompatActivity.requestDefaultApp(onDefaultAppResult: ActivityResultLauncher<Intent>) {
    val intent = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        val roleManager = getSystemService(RoleManager::class.java)
        roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
    } else {
        val setSmsAppIntent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
        setSmsAppIntent
    }
    onDefaultAppResult.launch(intent)
}