package com.sendhur.contactsapp.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sendhur.contactsapp.data.local.ContactEntity
import com.sendhur.contactsapp.data.local.ContactsDatabase
import com.sendhur.contactsapp.data.mapper.toContactEntity
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class ContactRemoteMediator(
    private val contactsDatabase: ContactsDatabase,
    private val contactsRepository: ContactsRepository
) : RemoteMediator<Int, ContactEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ContactEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }
            val contacts = contactsRepository.getContacts(
                page = loadKey,
                results = state.config.pageSize
            )
            contactsDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    contactsDatabase.dao.clearAll()
                }
                val contactEntities = contacts.results.map { it.toContactEntity() }
                contactsDatabase.dao.upsertAll(contactEntities)
                MediatorResult.Success(
                    endOfPaginationReached = contacts.results.isEmpty()
                )
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

}