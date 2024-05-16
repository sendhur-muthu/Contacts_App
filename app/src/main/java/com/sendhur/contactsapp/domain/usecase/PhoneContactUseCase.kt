package com.sendhur.contactsapp.domain.usecase

import com.sendhur.contactsapp.data.mapper.toContact
import com.sendhur.contactsapp.data.mapper.toContactEntity
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import javax.inject.Inject

class PhoneContactUseCase @Inject constructor(
    private val repository: ContactsRepository
) {
    suspend operator fun invoke(list: List<Contact>): List<Contact> {
        return repository.upsertPhoneContacts(list.map { it.toContactEntity() })
            .map { it.toContact() }
    }
}