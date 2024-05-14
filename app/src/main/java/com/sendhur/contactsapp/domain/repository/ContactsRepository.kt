package com.sendhur.contactsapp.domain.repository

import com.sendhur.contactsapp.data.remote.dto.ContactsDto

interface ContactsRepository {
    suspend fun getContacts(page: Int, results: Int) : ContactsDto
}