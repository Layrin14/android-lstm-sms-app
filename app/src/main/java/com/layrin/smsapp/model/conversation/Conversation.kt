package com.layrin.smsapp.model.conversation

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.layrin.smsapp.common.ItemComparator
import java.io.Serializable

@Entity(tableName = "conversation")
data class Conversation(
    @PrimaryKey
    val number: String,
    var time: Long = 0,
    var label: Int = -1,
    var probability: Array<Float> = Array(3) { 0F },
    var read: Boolean = true,
    var latestMessage: String? = "",
) : Serializable, ItemComparator {

    override val id: Any
        get() = number

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Conversation

        if (time != other.time) return false
        if (read != other.read) return false

        return true
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun hashCode(): Int {
        return number.hashCode()
    }
}