package com.layrin.smsapp.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T: Any>(
    view: View
): RecyclerView.ViewHolder(view) {

    abstract fun onBind(data: T)

}