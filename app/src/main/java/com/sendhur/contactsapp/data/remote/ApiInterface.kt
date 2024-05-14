package com.sendhur.contactsapp.data.remote

import com.sendhur.contactsapp.data.remote.dto.ContactsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("api/")
    suspend fun getContacts(
        @Query("page") page: Int,
        @Query("results") results: Int,
        @Query("inc") include: String = "gender,name,picture,phone,cell,id,email"
    ): ContactsDto
}