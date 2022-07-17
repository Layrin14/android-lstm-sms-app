package com.layrin.smsapp.ui.conversation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.layrin.smsapp.common.BaseViewHolder
import com.layrin.smsapp.common.DateTimeProvider
import com.layrin.smsapp.databinding.LayoutMessageInBinding
import com.layrin.smsapp.databinding.LayoutMessageOutBinding
import com.layrin.smsapp.model.message.Message

class MessageSentRecyclerViewHolder(
    private val binding: LayoutMessageOutBinding,
    context: Context
) : BaseViewHolder<Message>(binding.root) {
    private val dateTimeProvider = DateTimeProvider(context)
    override fun onBind(data: Message) {
        binding.apply {
            tvMessageText.text = data.text
            tvMessageTime.text = dateTimeProvider.getTimeFull(data.time)
            tvMessageText.alpha = 0f
            tvSentStatus.visibility = View.VISIBLE
            tvSentStatus.text = "Sent"
        }
    }

    companion object {
        fun from(parent: ViewGroup, context: Context): MessageSentRecyclerViewHolder {
            val view = LayoutMessageOutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MessageSentRecyclerViewHolder(view, context)
        }
    }
}

class MessageReceiveRecyclerViewHolder(
    private val binding: LayoutMessageInBinding,
    context: Context
) : BaseViewHolder<Message>(binding.root) {
    private val dateTimeProvider = DateTimeProvider(context)
    override fun onBind(data: Message) {
        binding.apply {
            tvMessageText.text = data.text
            tvMessageTime.text = dateTimeProvider.getTimeFull(data.time)
        }
    }

    companion object {
        fun from(parent: ViewGroup, context: Context): MessageReceiveRecyclerViewHolder {
            val view = LayoutMessageInBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MessageReceiveRecyclerViewHolder(view, context)
        }
    }
}