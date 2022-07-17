package com.layrin.smsapp.common

interface ItemComparator {
    val id: Any?

    fun areItemsTheSame(item: ItemComparator) = this.id == item.id

    fun areContentsTheSame(item: ItemComparator) = this == item
}