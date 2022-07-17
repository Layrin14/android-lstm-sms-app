package com.layrin.smsapp.ui.conversation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.layrin.smsapp.common.BaseRecyclerViewAdapter
import com.layrin.smsapp.common.BaseViewHolder
import com.layrin.smsapp.common.GenericDiffCallback
import com.layrin.smsapp.common.OnItemClickListener
import com.layrin.smsapp.databinding.LayoutContactListBinding
import com.layrin.smsapp.model.contact.Contact

class ContactListRecyclerViewAdapter(
    context: Context,
    diffCallback: GenericDiffCallback<Contact> = GenericDiffCallback(),
    listener: OnItemClickListener<Contact>,
) : BaseRecyclerViewAdapter<Contact, ContactListRecyclerViewAdapter.ContactListRecyclerViewHolder>(
    context,
    diffCallback,
    listener
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ContactListRecyclerViewHolder {
        val view = LayoutContactListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactListRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactListRecyclerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.onBind(currentItem)
    }

    inner class ContactListRecyclerViewHolder(
        private val binding: LayoutContactListBinding,
    ) : BaseViewHolder<Contact>(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick(this)
            }
        }

        override fun onBind(data: Contact) {
            binding.apply {
                tvContactName.text = data.name
                tvContactNumber.text = data.number
            }
        }
    }
}