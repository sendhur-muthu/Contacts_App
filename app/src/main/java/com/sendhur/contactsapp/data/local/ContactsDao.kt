package com.sendhur.contactsapp.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Upsert
    suspend fun upsertAll(contacts: List<ContactEntity>)

    @Query("SELECT * FROM contactentity WHERE isPhoneContact = 0")
    fun pagingSource(): PagingSource<Int, ContactEntity>

    @Query("DELETE FROM contactentity WHERE isPhoneContact = 0")
    fun deleteRandomContacts()

    @Query("SELECT * FROM contactentity WHERE name LIKE :query OR phone LIKE :query")
    suspend fun searchContacts(query: String): List<ContactEntity>

    @Query("DELETE FROM contactentity WHERE isPhoneContact = 1")
    fun deletePhoneContacts()

    @Query("SELECT * FROM contactentity WHERE isPhoneContact = 1")
    suspend fun getPhoneContacts(): List<ContactEntity>

    @Query("SELECT * FROM contactentity WHERE id = :id")
    suspend fun getContact(id: Int): ContactEntity?

    @Upsert
    suspend fun upsertContact(contactEntity: ContactEntity)
}