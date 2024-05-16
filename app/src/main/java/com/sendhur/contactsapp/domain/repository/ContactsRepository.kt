package com.sendhur.contactsapp.domain.repository

import com.sendhur.contactsapp.data.local.ContactEntity
import com.sendhur.contactsapp.data.remote.dto.ContactsDto

interface ContactsRepository {
    suspend fun getContacts(page: Int, results: Int): ContactsDto

    suspend fun searchContacts(query: String): List<ContactEntity>

    suspend fun upsertPhoneContacts(list: List<ContactEntity>): List<ContactEntity>

    suspend fun getContact(id: Int): ContactEntity?

    suspend fun updateContact(contactEntity: ContactEntity)

}