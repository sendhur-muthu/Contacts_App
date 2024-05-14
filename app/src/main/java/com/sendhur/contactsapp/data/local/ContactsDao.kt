package com.sendhur.contactsapp.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ContactsDao {
    @Upsert
    suspend fun upsertAll(contacts: List<ContactEntity>)

    @Query("SELECT * FROM contactentity WHERE isPhoneContact = 0")
    fun pagingSource(): PagingSource<Int, ContactEntity>

    @Query("DELETE FROM contactentity")
    fun clearAll()
}