package com.sendhur.contactsapp.domain.usecase

import com.sendhur.contactsapp.data.mapper.toContact
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import javax.inject.Inject

class SearchContactsUseCase @Inject constructor(
    private val contactsRepository: ContactsRepository
) {
    suspend operator fun invoke(query: String): List<Contact> {
        return contactsRepository.searchContacts(query).map {
            it.toContact()
        }
    }
}