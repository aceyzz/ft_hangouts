package com.cedmulle.ft_hangouts

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private var messages: List<Message>, private var sentColor: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textMessage: TextView = view.findViewById(R.id.textMessage)
        val textTime: TextView = view.findViewById(R.id.textTime)
    }

    class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textMessage: TextView = view.findViewById(R.id.textMessage)
        val textTime: TextView = view.findViewById(R.id.textTime)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeString = sdf.format(Date(message.timestamp))

        if (holder is SentMessageViewHolder) {
            holder.textMessage.text = message.content
            holder.textTime.text = timeString
            holder.textMessage.backgroundTintList = ColorStateList.valueOf(sentColor)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.textMessage.text = message.content
            holder.textTime.text = timeString
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateData(newMessages: List<Message>, newColor: Int) {
        this.sentColor = newColor
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = messages.size
            override fun getNewListSize() = newMessages.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = messages[oldItemPosition].id == newMessages[newItemPosition].id
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = messages[oldItemPosition] == newMessages[newItemPosition]
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.messages = newMessages
        diffResult.dispatchUpdatesTo(this)
    }
}
