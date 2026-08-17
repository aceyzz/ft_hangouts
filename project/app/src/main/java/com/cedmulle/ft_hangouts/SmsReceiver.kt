package com.cedmulle.ft_hangouts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val dbHelper = DatabaseHelper(context)

            for (sms in messages) {
                val sender = sms.displayOriginatingAddress ?: continue
                val body = sms.displayMessageBody ?: continue

                var contact = dbHelper.getContactByPhone(sender)

                if (contact == null) {
                    val newContact = Contact(
                        firstName = sender,
                        lastName = "",
                        phone = sender,
                        email = "",
                        address = ""
                    )
                    val id = dbHelper.addContact(newContact)
                    contact = dbHelper.getContact(id.toInt())
                }

                if (contact != null) {
                    val message = Message(
                        contactId = contact.id,
                        content = body,
                        timestamp = System.currentTimeMillis(),
                        isSent = false
                    )
                    dbHelper.addMessage(message)

                    val updateIntent = Intent("com.cedmulle.ft_hangouts.NEW_MESSAGE")
                    updateIntent.setPackage(context.packageName)
                    context.sendBroadcast(updateIntent)
                }
            }
        }
    }
}
