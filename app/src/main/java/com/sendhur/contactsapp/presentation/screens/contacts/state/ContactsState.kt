package com.sendhur.contactsapp.presentation.screens.contacts.state

import com.sendhur.contactsapp.domain.model.Contact

data class ContactsState(
    val isLoading: Boolean = false,
    val contacts: List<Contact> = emptyList(),
    val error: String = ""
)
