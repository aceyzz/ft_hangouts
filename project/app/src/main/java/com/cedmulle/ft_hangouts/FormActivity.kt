package com.cedmulle.ft_hangouts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FormActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var contactId = -1
    private var currentPhotoUri: String? = null
    private lateinit var imageProfilePreview: ImageView

    private val pickImage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            currentPhotoUri = uri.toString()
            imageProfilePreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form)

        dbHelper = DatabaseHelper(this)

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

        val mainLayout = findViewById<View>(R.id.main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val editFirstName = findViewById<EditText>(R.id.editFirstName)
        val editLastName = findViewById<EditText>(R.id.editLastName)
        val editPhone = findViewById<EditText>(R.id.editPhone)
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editAddress = findViewById<EditText>(R.id.editAddress)
        val btnSave = findViewById<Button>(R.id.btnSave)
        imageProfilePreview = findViewById(R.id.imageProfilePreview)

        contactId = intent.getIntExtra("CONTACT_ID", -1)
        if (contactId != -1) {
            val contact = dbHelper.getContact(contactId)
            if (contact != null) {
                editFirstName.setText(contact.firstName)
                editLastName.setText(contact.lastName)
                editPhone.setText(contact.phone)
                editEmail.setText(contact.email)
                editAddress.setText(contact.address)
                currentPhotoUri = contact.photoUri
                if (currentPhotoUri != null) {
                    imageProfilePreview.setImageURI(Uri.parse(currentPhotoUri))
                }
            }
        }

        imageProfilePreview.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }

        btnSave.setOnClickListener {
            val firstName = editFirstName.text.toString().trim()
            val lastName = editLastName.text.toString().trim()
            val phone = editPhone.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val address = editAddress.text.toString().trim()

            if (firstName.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "First Name and Phone are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contact = Contact(
                id = if (contactId != -1) contactId else -1,
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                email = email,
                address = address,
                photoUri = currentPhotoUri
            )

            if (contactId != -1) {
                dbHelper.updateContact(contact)
            } else {
                dbHelper.addContact(contact)
            }
            finish()
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
