package com.layrin.smsapp.ui.main

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.layrin.smsapp.R
import com.layrin.smsapp.common.*
import com.layrin.smsapp.databinding.LayoutMainConversationBinding
import com.layrin.smsapp.model.conversation.Conversation

class ConversationRecyclerViewAdapter(
    private val context: Context,
    diffCallback: GenericDiffCallback<Conversation> = GenericDiffCallback(),
    listener: OnItemClickListener<Conversation>
) : BasePagingAdapter<Conversation, ConversationRecyclerViewAdapter.ConversationRecyclerViewHolder>(
    context,
    diffCallback,
    listener
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ConversationRecyclerViewHolder {
        val view = LayoutMainConversationBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return ConversationRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationRecyclerViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.onBind(currentItem)
        }
        holder.itemView.apply {
            if (isSelected(holder.absoluteAdapterPosition)) setBackgroundColor(selectedItemColor)
            else setBackgroundColor(defaultBackground)
        }
    }

    inner class ConversationRecyclerViewHolder(
        private val binding: LayoutMainConversationBinding,
    ) : BaseViewHolder<Conversation>(binding.root) {

        private val dateTimeProvider = DateTimeProvider(context)

        init {
            itemView.setOnClickListener {
                onItemClick(this)
            }
            itemView.setOnLongClickListener {
                onItemLongClick(this)
            }
        }

        override fun onBind(data: Conversation) {
            val number = data.number
            binding.tvSender.text = number
            binding.tvTime.text = dateTimeProvider.getTimeCondensed(data.time)
            val str = SpannableString(data.latestMessage?.replace("\n", " "))
            if (!data.read) {
                str.setSpan(StyleSpan(Typeface.BOLD),
                    0,
                    str.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                str.setSpan(ForegroundColorSpan(context.getColor(R.color.black)),
                    0,
                    str.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            binding.tvLastMsg.text = str
        }
    }
}
//    }
//}
