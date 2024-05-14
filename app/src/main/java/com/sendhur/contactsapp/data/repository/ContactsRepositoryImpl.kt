package com.sendhur.contactsapp.data.repository

import com.sendhur.contactsapp.data.remote.ApiInterface
import com.sendhur.contactsapp.data.remote.dto.ContactsDto
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import javax.inject.Inject

class ContactsRepositoryImpl @Inject constructor(
    private val api: ApiInterface
) : ContactsRepository {
    override suspend fun getContacts(page: Int, results: Int): ContactsDto {
        return api.getContacts(page, results)
    }
}