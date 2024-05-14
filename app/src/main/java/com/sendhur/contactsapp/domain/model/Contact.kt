package com.sendhur.contactsapp.domain.model

data class Contact(
    val id: Int = 0,
    val cell: String = "",
    val email: String = "",
    val gender: String = "",
    val name: String = "",
    val phone: String = "",
    val picture: String = "",
    val isPhoneContact: Boolean = false
)
