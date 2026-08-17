package com.cedmulle.ft_hangouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.core.net.toUri

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onContactClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.textName)
        val textPhone: TextView = view.findViewById(R.id.textPhone)
        val imageProfile: ImageView = view.findViewById(R.id.imageProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        holder.textName.text = holder.itemView.context.getString(R.string.contact_name_format, contact.firstName, contact.lastName)
        holder.textPhone.text = contact.phone

        holder.imageProfile.setImageURI(contact.photoUri?.toUri())

        holder.itemView.setOnClickListener {
            onContactClick(contact)
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun updateData(newContacts: List<Contact>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = contacts.size
            override fun getNewListSize() = newContacts.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return contacts[oldItemPosition].id == newContacts[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return contacts[oldItemPosition] == newContacts[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.contacts = newContacts
        diffResult.dispatchUpdatesTo(this)
    }
}
