package com.sendhur.contactsapp.presentation.screens.contacts.state

import com.sendhur.contactsapp.domain.model.Contact

data class SearchState(
    val contacts: List<Contact> = emptyList()
)