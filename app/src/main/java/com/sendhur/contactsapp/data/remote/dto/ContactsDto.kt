package com.sendhur.contactsapp.data.remote.dto

import com.sendhur.contactsapp.data.local.ContactEntity
import com.sendhur.contactsapp.domain.model.Contact

data class ContactsDto(
    val info: Info = Info(),
    val results: List<Result> = listOf()
) {
    data class Info(
        val page: Int = 0,
        val results: Int = 0,
        val seed: String = "",
        val version: String = ""
    )

    data class Result(
        val cell: String = "",
        val email: String = "",
        val gender: String = "",
        val id: Id = Id(),
        val name: Name = Name(),
        val phone: String = "",
        val picture: Picture = Picture()
    ) {
        data class Id(
            val name: String = "",
            val value: String = ""
        )

        data class Name(
            val first: String = "",
            val last: String = "",
            val title: String = ""
        )

        data class Picture(
            val large: String = "",
            val medium: String = "",
            val thumbnail: String = ""
        )
    }
}