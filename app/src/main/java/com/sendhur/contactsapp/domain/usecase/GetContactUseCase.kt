package com.sendhur.contactsapp.domain.usecase

import com.sendhur.contactsapp.data.mapper.toContact
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import javax.inject.Inject

class GetContactUseCase @Inject constructor(
    private val repository: ContactsRepository
) {
    suspend operator fun invoke(id: Int): Contact? {
        return repository.getContact(id)?.toContact()
    }
}