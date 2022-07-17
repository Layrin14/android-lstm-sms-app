package com.layrin.smsapp.model.message

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.layrin.smsapp.common.ItemComparator

@Entity(tableName = "messages")
data class Message(
    val text: String,
    var time: Long,
    @PrimaryKey(autoGenerate = true)
    override var id: Int? = -1,
    val number: String,
    var type: Int,
    var sent: Boolean = false
) : ItemComparator {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message
        if (text != other.text) return false
        if (time != other.time) return false
        if (type != other.type) return false
        if (sent != other.sent) return false
        if (number != other.number) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
