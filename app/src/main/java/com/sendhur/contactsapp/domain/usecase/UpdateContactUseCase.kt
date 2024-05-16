package com.sendhur.contactsapp.domain.usecase

import com.sendhur.contactsapp.data.mapper.toContactEntity
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import javax.inject.Inject

class UpdateContactUseCase @Inject constructor(
    private val contactsRepository: ContactsRepository
) {
    suspend operator fun invoke(contact: Contact) {
        contactsRepository.updateContact(contact.toContactEntity())
    }
}