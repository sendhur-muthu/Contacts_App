package com.sendhur.contactsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cell: String = "",
    val email: String = "",
    val gender: String = "",
    val name: String = "",
    val phone: String = "",
    val picture: String = "",
    val isPhoneContact: Boolean = false
)
