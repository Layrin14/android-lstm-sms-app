package com.layrin.smsapp.model.conversation

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson

class ConversationTypeConverter {
    @TypeConverter
    fun toJson(value: Array<Float>?): String {
        Log.i("toJson", "$value")
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJson(value: String?): Array<Float> {
        Log.i("fromJson", "${Gson().fromJson(value, Array<Float>::class.java)}")
        return Gson().fromJson(value, Array<Float>::class.java)
    }
}