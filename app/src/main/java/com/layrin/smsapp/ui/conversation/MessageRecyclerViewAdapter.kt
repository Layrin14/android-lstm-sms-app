package com.layrin.smsapp.ui.conversation

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.layrin.smsapp.common.BasePagingAdapter
import com.layrin.smsapp.common.GenericDiffCallback
import com.layrin.smsapp.common.OnItemClickListener
import com.layrin.smsapp.model.message.Message

class MessageRecyclerViewAdapter(
    private val context: Context,
    diffCallback: GenericDiffCallback<Message> = GenericDiffCallback(),
    listener: OnItemClickListener<Message>
) : BasePagingAdapter<Message, RecyclerView.ViewHolder>(
    context,
    diffCallback,
    listener
) {

    companion object {
        const val ITEM_VIEW_TYPE_SENT = 0
        const val ITEM_VIEW_TYPE_RECEIVE = 1
    }

    override fun getItemViewType(position: Int): Int = if (getItem(position)?.type == ITEM_VIEW_TYPE_RECEIVE) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_RECEIVE -> MessageReceiveRecyclerViewHolder.from(parent, context)
            ITEM_VIEW_TYPE_SENT -> MessageSentRecyclerViewHolder.from(parent, context)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = getItem(position)!!
        when (holder) {
            is MessageSentRecyclerViewHolder -> {
                holder.onBind(currentItem)
                holderClick(holder)
            }
            is MessageReceiveRecyclerViewHolder -> {
                holder.onBind(currentItem)
                holderClick(holder)
            }
        }
    }

    private fun holderClick(holder: RecyclerView.ViewHolder) {
        holder.itemView.apply {
            if (isSelected(holder.absoluteAdapterPosition)) setBackgroundColor(selectedItemColor)
            else setBackgroundColor(defaultBackground)
            setOnClickListener {
                onItemClick(holder)
            }
            setOnLongClickListener {
                onItemLongClick(holder)
            }
        }
    }
}