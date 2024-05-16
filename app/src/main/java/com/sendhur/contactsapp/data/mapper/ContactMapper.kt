package com.sendhur.contactsapp.data.mapper

import com.sendhur.contactsapp.data.local.ContactEntity
import com.sendhur.contactsapp.data.remote.dto.ContactsDto
import com.sendhur.contactsapp.domain.model.Contact

fun ContactsDto.Result.toContact(): Contact {
    return Contact(
        cell = cell,
        email = email,
        gender = gender,
        name = "${name.first} ${name.last}",
        phone = phone,
        picture = picture.medium,
        isPhoneContact = false
    )
}

fun ContactsDto.Result.toContactEntity(): ContactEntity {
    return ContactEntity(
        cell = cell,
        email = email,
        gender = gender,
        name = "${name.first} ${name.last}",
        phone = phone,
        picture = picture.medium,
        isPhoneContact = false
    )
}

fun ContactEntity.toContact(): Contact {
    return Contact(
        id = id,
        cell = cell,
        email = email,
        gender = gender,
        name = name,
        phone = phone,
        picture = picture,
        isPhoneContact = isPhoneContact
    )
}

fun Contact.toContactEntity(): ContactEntity {
    return ContactEntity(
        id = id,
        cell = cell,
        email = email,
        gender = gender,
        name = name,
        phone = phone,
        picture = picture,
        isPhoneContact = isPhoneContact
    )
}