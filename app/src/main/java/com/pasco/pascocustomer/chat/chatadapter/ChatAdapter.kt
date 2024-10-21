package com.pasco.pascocustomer.chat.chatadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pasco.pascocustomer.R
import com.pasco.pascocustomer.chat.Message
import com.pasco.pascocustomer.databinding.ItemMessageReceivedBinding
import com.pasco.pascocustomer.databinding.ItemMessageSentBinding

class ChatAdapter(private val messages: List<Message>, private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding =
                ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentMessageViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class SentMessageViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            if (message.text != null) {
                binding.textMessage.visibility = View.VISIBLE
                binding.textMessage.text = message.text
                binding.voiceMessage.visibility = View.GONE
            } else if (message.imageUrl != null) {
                binding.messageImageView.visibility = View.VISIBLE
                Glide.with(binding.messageImageView.context).load(message.imageUrl).into(binding.messageImageView)
                binding.textMessage.visibility = View.GONE
            } else if (message.voiceUrl != null) {

                binding.voiceMessage.visibility = View.VISIBLE
                binding.voiceMessage.setOnClickListener {
                    //playVoiceMessage(message.voiceUrl)
                }
                binding.textMessage.visibility = View.GONE
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textMessage.text = message.text
            if (!message.text.isNullOrEmpty()) {
                binding.textMessage.text = message.text
                binding.textMessage.visibility = View.VISIBLE
            } else {
                binding.textMessage.visibility = View.GONE
            }

            if (!message.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.messageImageView.context)
                    .load(message.imageUrl)
                    .into(binding.messageImageView)
                binding.messageImageView.visibility = View.VISIBLE
            } else {
                binding.messageImageView.visibility = View.GONE
            }

            if (!message.voiceUrl.isNullOrEmpty()) {
                binding.messageVoiceTextView.text = "Voice message: ${message.voiceUrl}"
                binding.messageVoiceTextView.visibility = View.VISIBLE
            } else {
                binding.messageVoiceTextView.visibility = View.GONE
            }
        }
    }
}