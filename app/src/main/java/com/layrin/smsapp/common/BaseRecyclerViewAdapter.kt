package com.layrin.smsapp.common

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.layrin.smsapp.R

abstract class BaseRecyclerViewAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    context: Context,
    diffCallback: DiffUtil.ItemCallback<T>,
    private val listener: OnItemClickListener<T>
) : ListAdapter<T, VH>(diffCallback){

    private var selected = arrayListOf<Int>()

    val isActive get() = selected.size > 0

    fun isSelected(pos: Int): Boolean = selected.contains(pos)

    private fun toggleSelectedItem(pos: Int) {
        if (selected.contains(pos)) selected.remove(pos)
        else selected.add(pos)
        listener.mainInterface(selected.size)
    }

    val selectedItemColor = context.getColor(R.color.selectionBackground)
    val defaultBackground = context.getColor(R.color.primaryTextColor)

    fun onItemClick(viewHolder: VH) {
        val position = viewHolder.absoluteAdapterPosition
        if (isActive) {
            toggleSelectedItem(position)
            if (isSelected(position)) viewHolder.itemView.setBackgroundColor(selectedItemColor)
            else viewHolder.itemView.setBackgroundColor(defaultBackground)
        } else {
            listener.onItemClick(viewHolder.itemView, getItem(position))
        }
    }

    fun onItemLongClick(viewHolder: VH): Boolean {
        val position = viewHolder.absoluteAdapterPosition
        toggleSelectedItem(position)
        if (isSelected(position)) viewHolder.itemView.setBackgroundColor(selectedItemColor)
        else viewHolder.itemView.setBackgroundColor(defaultBackground)
        return true
    }

    fun close() {
        if (!isActive) return
        val listCopy = selected.toList()
        selected.clear()
        for (item in listCopy) notifyItemChanged(item)
    }

}


