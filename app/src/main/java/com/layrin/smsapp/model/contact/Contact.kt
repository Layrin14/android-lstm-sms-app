package com.layrin.smsapp.model.contact

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.layrin.smsapp.common.ItemComparator
import kotlin.math.abs

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey
    val number: String,
    val name: String,
    val contactId: Int
) : ItemComparator {
    override val id: Any
        get() = number

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false

        other as Contact
        return number == other.number
    }

    override fun hashCode(): Int = abs(number.hashCode())
}
