package com.sendhur.contactsapp.presentation.screens.contacts.components

import com.sendhur.contactsapp.domain.model.Contact

data class PhoneContactsState(
    val phoneContacts: List<Contact> = emptyList(),
    val phoneContactsError: String = ""
)