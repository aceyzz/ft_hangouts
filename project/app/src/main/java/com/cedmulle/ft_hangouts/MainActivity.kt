package com.cedmulle.ft_hangouts

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ContactAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var textEmpty: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var fabAddContact: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewContacts)
        textEmpty = findViewById(R.id.textEmpty)
        fabAddContact = findViewById(R.id.fabAddContact)

        val headerColor = dbHelper.getHeaderColor() ?: getColor(R.color.primaryColor)
        toolbar.setBackgroundColor(headerColor)
        fabAddContact.backgroundTintList = ColorStateList.valueOf(headerColor)

        val mainLayout = findViewById<View>(R.id.main_layout)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(emptyList()) { contact ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("CONTACT_ID", contact.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        fabAddContact.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val colorRes = when (item.itemId) {
            R.id.color_red -> R.color.header_red
            R.id.color_green -> R.color.header_green
            R.id.color_blue -> R.color.header_blue
            R.id.color_dark -> R.color.header_dark
            else -> return super.onOptionsItemSelected(item)
        }

        val resolvedColor = getColor(colorRes)
        toolbar.setBackgroundColor(resolvedColor)
        fabAddContact.backgroundTintList = ColorStateList.valueOf(resolvedColor)
        dbHelper.saveHeaderColor(resolvedColor)

        return true
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
    }

    private fun loadContacts() {
        val contacts = dbHelper.getAllContacts()

        if (contacts.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.updateData(contacts)
        }
    }
}
