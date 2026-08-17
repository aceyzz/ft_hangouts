package com.cedmulle.ft_hangouts

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_CONTACTS = "contacts"
        const val COLUMN_ID = "id"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        const val COLUMN_PHONE = "phone_number"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_ADDRESS = "address"
        const val COLUMN_PHOTO_URI = "photo_uri"

        const val TABLE_SETTINGS = "settings"
        const val COLUMN_SETTING_KEY = "setting_key"
        const val COLUMN_SETTING_VALUE = "setting_value"
        const val KEY_HEADER_COLOR = "header_color"

        const val TABLE_MESSAGES = "messages"
        const val COLUMN_MSG_ID = "id"
        const val COLUMN_MSG_CONTACT_ID = "contact_id"
        const val COLUMN_MSG_CONTENT = "content"
        const val COLUMN_MSG_TIMESTAMP = "timestamp"
        const val COLUMN_MSG_IS_SENT = "is_sent"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createContactsTable = ("CREATE TABLE " + TABLE_CONTACTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FIRST_NAME + " TEXT NOT NULL, "
                + COLUMN_LAST_NAME + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT NOT NULL, "
                + COLUMN_EMAIL + " TEXT NOT NULL, "
                + COLUMN_ADDRESS + " TEXT NOT NULL, "
                + COLUMN_PHOTO_URI + " TEXT)")
        db.execSQL(createContactsTable)

        val createSettingsTable = ("CREATE TABLE " + TABLE_SETTINGS + " ("
                + COLUMN_SETTING_KEY + " TEXT PRIMARY KEY, "
                + COLUMN_SETTING_VALUE + " INTEGER NOT NULL)")
        db.execSQL(createSettingsTable)

        val createMessagesTable = ("CREATE TABLE " + TABLE_MESSAGES + " ("
                + COLUMN_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MSG_CONTACT_ID + " INTEGER NOT NULL, "
                + COLUMN_MSG_CONTENT + " TEXT NOT NULL, "
                + COLUMN_MSG_TIMESTAMP + " INTEGER NOT NULL, "
                + COLUMN_MSG_IS_SENT + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_MSG_CONTACT_ID + ") REFERENCES " + TABLE_CONTACTS + "(" + COLUMN_ID + ") ON DELETE CASCADE)")
        db.execSQL(createMessagesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun saveHeaderColor(color: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SETTING_KEY, KEY_HEADER_COLOR)
            put(COLUMN_SETTING_VALUE, color)
        }
        db.replace(TABLE_SETTINGS, null, values)
        db.close()
    }

    fun getHeaderColor(): Int? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_SETTING_VALUE FROM $TABLE_SETTINGS WHERE $COLUMN_SETTING_KEY = ?", arrayOf(KEY_HEADER_COLOR))
        var color: Int? = null
        if (cursor.moveToFirst()) {
            color = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return color
    }

    fun addContact(contact: Contact): Long {
        val db = this.writableDatabase
        val defaultUri = "android.resource://${context.packageName}/drawable/default_user"
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, contact.firstName)
            put(COLUMN_LAST_NAME, contact.lastName)
            put(COLUMN_PHONE, contact.phone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_ADDRESS, contact.address)
            put(COLUMN_PHOTO_URI, contact.photoUri ?: defaultUri)
        }
        val id = db.insert(TABLE_CONTACTS, null, values)
        db.close()
        return id
    }

    fun getContact(id: Int): Contact? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CONTACTS WHERE $COLUMN_ID = ?", arrayOf(id.toString()))
        var contact: Contact? = null
        if (cursor.moveToFirst()) {
            contact = Contact(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI))
            )
        }
        cursor.close()
        db.close()
        return contact
    }

    fun getContactByPhone(phone: String): Contact? {
        val contacts = getAllContacts()
        val targetPhone = phone.filter { it.isDigit() || it == '+' }
        for (contact in contacts) {
            val normalizedDbPhone = contact.phone.filter { it.isDigit() || it == '+' }
            if (normalizedDbPhone == targetPhone) {
                return contact
            }
        }
        return null
    }

    fun getAllContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val contact = Contact(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                    lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                    photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI))
                )
                contactList.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return contactList
    }

    fun updateContact(contact: Contact): Int {
        val db = this.writableDatabase
        val defaultUri = "android.resource://${context.packageName}/drawable/default_user"
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, contact.firstName)
            put(COLUMN_LAST_NAME, contact.lastName)
            put(COLUMN_PHONE, contact.phone)
            put(COLUMN_EMAIL, contact.email)
            put(COLUMN_ADDRESS, contact.address)
            put(COLUMN_PHOTO_URI, contact.photoUri ?: defaultUri)
        }
        val success = db.update(TABLE_CONTACTS, values, "$COLUMN_ID=?", arrayOf(contact.id.toString()))
        db.close()
        return success
    }

    fun deleteContact(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_CONTACTS, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }

    fun addMessage(message: Message) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MSG_CONTACT_ID, message.contactId)
            put(COLUMN_MSG_CONTENT, message.content)
            put(COLUMN_MSG_TIMESTAMP, message.timestamp)
            put(COLUMN_MSG_IS_SENT, if (message.isSent) 1 else 0)
        }
        db.insert(TABLE_MESSAGES, null, values)
        db.close()
    }

    fun getMessagesForContact(contactId: Int): List<Message> {
        val messageList = mutableListOf<Message>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_MESSAGES WHERE $COLUMN_MSG_CONTACT_ID = ? ORDER BY $COLUMN_MSG_TIMESTAMP ASC",
            arrayOf(contactId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val message = Message(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_ID)),
                    contactId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_CONTACT_ID)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_CONTENT)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_MSG_TIMESTAMP)),
                    isSent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_IS_SENT)) == 1
                )
                messageList.add(message)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return messageList
    }
}
