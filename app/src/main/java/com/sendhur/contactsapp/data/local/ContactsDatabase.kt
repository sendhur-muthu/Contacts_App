package com.sendhur.contactsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ContactEntity::class],
    version = 1
)
abstract class ContactsDatabase : RoomDatabase() {
    abstract val dao: ContactsDao
}