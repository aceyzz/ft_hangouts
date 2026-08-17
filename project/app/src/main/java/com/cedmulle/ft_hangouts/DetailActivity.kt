package com.cedmulle.ft_hangouts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var contactId: Int = -1
    private var currentPhone: String = ""

    private val callPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            makePhoneCall()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        dbHelper = DatabaseHelper(this)
        contactId = intent.getIntExtra("CONTACT_ID", -1)

        if (contactId == -1) {
            finish()
            return
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        val headerColor = dbHelper.getHeaderColor() ?: getColor(R.color.primaryColor)
        toolbar.setBackgroundColor(headerColor)
        val colorStateList = ColorStateList.valueOf(headerColor)
        findViewById<FloatingActionButton>(R.id.btnCall).backgroundTintList = colorStateList
        findViewById<FloatingActionButton>(R.id.btnSms).backgroundTintList = colorStateList
        findViewById<FloatingActionButton>(R.id.btnEdit).backgroundTintList = colorStateList
        findViewById<FloatingActionButton>(R.id.btnDelete).backgroundTintList = colorStateList

        val mainLayout = findViewById<View>(R.id.main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        findViewById<FloatingActionButton>(R.id.btnEdit).setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra("CONTACT_ID", contactId)
            startActivity(intent)
        }

        findViewById<FloatingActionButton>(R.id.btnDelete).setOnClickListener {
            dbHelper.deleteContact(contactId)
            finish()
        }

        findViewById<FloatingActionButton>(R.id.btnCall).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }

        findViewById<FloatingActionButton>(R.id.btnSms).setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("CONTACT_ID", contactId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val contact = dbHelper.getContact(contactId)
        if (contact != null) {
            currentPhone = contact.phone
            findViewById<TextView>(R.id.textName).text = getString(R.string.contact_name_format, contact.firstName, contact.lastName)
            findViewById<TextView>(R.id.textPhone).text = contact.phone
            findViewById<TextView>(R.id.textEmail).text = contact.email
            findViewById<TextView>(R.id.textAddress).text = contact.address

            val imageView = findViewById<ImageView>(R.id.imageProfile)
            if (contact.photoUri != null) {
                imageView.setImageURI(Uri.parse(contact.photoUri))
            } else {
                imageView.setImageResource(R.drawable.ic_launcher_background)
            }
        } else {
            finish()
        }
    }

    private fun makePhoneCall() {
        if (currentPhone.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL, "tel:$currentPhone".toUri())
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
