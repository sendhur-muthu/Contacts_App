package com.sendhur.contactsapp.presentation.screens.contact_detail.state

sealed class ContactDetailEvent {
    data class NameValueChanged(val text: String): ContactDetailEvent()
    data class PhoneValueChanged(val text: String): ContactDetailEvent()
    data class EmailValueChanged(val text: String): ContactDetailEvent()
    data object AddNewContact: ContactDetailEvent()
    data object SaveContact: ContactDetailEvent()
    data object EditClicked: ContactDetailEvent()
}