package com.sendhur.contactsapp.presentation.screens.contacts.state

import com.sendhur.contactsapp.domain.model.Contact

data class PhoneContactsState(
    val isLoading: Boolean = false,
    val phoneContacts: List<Contact> = emptyList(),
    val phoneContactsError: String = ""
)