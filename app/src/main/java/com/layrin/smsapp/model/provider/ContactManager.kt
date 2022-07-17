package com.layrin.smsapp.model.provider

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import com.layrin.smsapp.model.contact.Contact
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*
import kotlin.collections.HashMap

class ContactManager(
    private val context: Context,
) {

    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private val phoneUtil = PhoneNumberUtil.createInstance(context)
    private val map = HashMap<String, String>()

    fun getClean(number: String): String {
        return try {
            val countryIso = telephonyManager.networkCountryIso
            val numberProto = phoneUtil.parse(
                number,
                countryIso.uppercase(Locale.ROOT)
            )
            if (numberProto.nationalNumber.toString().length < 7) numberProto.nationalNumber.toString()
            else "+${numberProto.countryCode} ${numberProto.nationalNumber}"
        } catch (e: NumberParseException) {
            number.filter {
                it.isLetterOrDigit()
            }
        }
    }

    fun getContactHashMap(): HashMap<String, String> {
        val cr = context.contentResolver
        val cursor: Cursor? = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        while (cursor != null && cursor.moveToNext()) {
            val id = cursor.getString(
                cursor.getColumnIndex(ContactsContract.Contacts._ID)
            )
            val name = cursor.getString(
                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            )
            if (name != null && cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                val phoneCursor: Cursor? = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id),
                    null
                )
                while (phoneCursor != null && phoneCursor.moveToNext()) {
                    val phoneNo = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    map[getClean(phoneNo)] = name
                }
                phoneCursor?.close()
            }
        }
        cursor?.close()
        return map
    }

    @SuppressLint("Recycle", "Range")
    fun getContactList(): Array<Contact> {
        val list = arrayListOf<Contact>()
        val contentResolver = context.contentResolver

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            ContactsContract.Contacts.HAS_PHONE_NUMBER + "> 0 AND LENGTH (" +
                    ContactsContract.CommonDataKinds.Phone.NUMBER + ") > 0",
            null,
            "display_name ASC"
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                    ))
                val contact = ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI,
                    id.toLong()
                )
                val name = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    )
                )
                val number = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    )
                )
                list.add(Contact(getClean(number), name, id.toInt()))
            }
        }
        cursor?.close()
        val array = list.toTypedArray()
        array.sortBy { it.name.lowercase(Locale.ROOT) }
        return array
    }
}