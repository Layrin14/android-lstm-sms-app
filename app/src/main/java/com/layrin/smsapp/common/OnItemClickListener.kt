package com.layrin.smsapp.common

import android.view.View

interface OnItemClickListener<T: Any> {
    fun onItemClick(view: View, conversation: T)
    fun mainInterface(size: Int)
}