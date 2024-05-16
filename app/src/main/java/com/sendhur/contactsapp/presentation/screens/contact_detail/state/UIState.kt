package com.sendhur.contactsapp.presentation.screens.contact_detail.state

import com.sendhur.contactsapp.domain.model.Contact

sealed class UIState {
    data class onSaveClicked(val contact: Contact): UIState()
    data class onEditClicked(val contact: Contact): UIState()

    data class ShowError(val message: String): UIState()
}