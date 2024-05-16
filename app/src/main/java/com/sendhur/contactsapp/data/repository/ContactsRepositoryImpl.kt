package com.sendhur.contactsapp.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.sendhur.contactsapp.data.local.ContactEntity
import com.sendhur.contactsapp.data.local.ContactsDao
import com.sendhur.contactsapp.data.local.ContactsDatabase
import com.sendhur.contactsapp.data.remote.ApiInterface
import com.sendhur.contactsapp.data.remote.dto.ContactsDto
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val api: ApiInterface,
    private val contactsDao: ContactsDao,
    private val contactsDatabase: ContactsDatabase
) : ContactsRepository {
    override suspend fun getContacts(page: Int, results: Int): ContactsDto {
        return api.getContacts(page, results)
    }

    override suspend fun searchContacts(query: String): List<ContactEntity> {
        return contactsDao.searchContacts(query)
    }


    override suspend fun upsertPhoneContacts(list: List<ContactEntity>): List<ContactEntity> {
        var contacts = emptyList<ContactEntity>()
        contactsDatabase.withTransaction {
            contactsDao.deletePhoneContacts()
            contactsDao.upsertAll(list)
            contacts = contactsDao.getPhoneContacts()
        }
        return contacts
    }

    override suspend fun getContact(id: Int): ContactEntity? {
        return contactsDao.getContact(id)
    }

    override suspend fun updateContact(contactEntity: ContactEntity) {
        contactsDao.upsertContact(contactEntity)
    }
}