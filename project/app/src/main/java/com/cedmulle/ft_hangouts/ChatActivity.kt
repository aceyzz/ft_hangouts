package com.cedmulle.ft_hangouts

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.telephony.SmsManager
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private var contactId: Int = -1
    private var contactPhone: String = ""

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            loadMessages()
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        dbHelper = DatabaseHelper(this)
        contactId = intent.getIntExtra("CONTACT_ID", -1)

        val contact = dbHelper.getContact(contactId)
        if (contact == null) {
            finish()
            return
        }
        contactPhone = contact.phone

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.contact_name_format, contact.firstName, contact.lastName)

        val btnSend = findViewById<FloatingActionButton>(R.id.btnSend)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        val headerColor = dbHelper.getHeaderColor() ?: getColor(R.color.primaryColor)
        toolbar.setBackgroundColor(headerColor)
        btnSend.backgroundTintList = ColorStateList.valueOf(headerColor)

        val mainLayout = findViewById<View>(R.id.main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewMessages)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        adapter = MessageAdapter(emptyList(), headerColor)
        recyclerView.adapter = adapter

        val editMessage = findViewById<EditText>(R.id.editMessage)

        btnSend.setOnClickListener {
            val content = editMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendMessage(content)
                    editMessage.text.clear()
                } else {
                    requestPermissionsLauncher.launch(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS))
                }
            }
        }

        requestPermissionsLauncher.launch(arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS))
    }

    private fun sendMessage(content: String) {
        try {
            val smsManager = ContextCompat.getSystemService(this, SmsManager::class.java)
            smsManager?.sendTextMessage(contactPhone, null, content, null, null)

            val message = Message(
                contactId = contactId,
                content = content,
                timestamp = System.currentTimeMillis(),
                isSent = true
            )
            dbHelper.addMessage(message)
            loadMessages()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadMessages() {
        val messages = dbHelper.getMessagesForContact(contactId)
        val headerColor = dbHelper.getHeaderColor() ?: getColor(R.color.primaryColor)
        adapter.updateData(messages, headerColor)
        if (messages.isNotEmpty()) {
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }

    override fun onResume() {
        super.onResume()
        loadMessages()
        val filter = IntentFilter("com.cedmulle.ft_hangouts.NEW_MESSAGE")
        ContextCompat.registerReceiver(this, messageReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(messageReceiver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
