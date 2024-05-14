package com.sendhur.contactsapp.domain.usecase

import com.sendhur.contactsapp.common.Resource
import com.sendhur.contactsapp.data.mapper.toContact
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.domain.repository.ContactsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetContactsUseCase @Inject constructor(
    private val repository: ContactsRepository
) {
    operator fun invoke(page: Int, results: Int): Flow<Resource<List<Contact>>> = flow {
        try {
            emit(Resource.Loading())
            val contacts = repository.getContacts(page = page, results = results).results.map { it.toContact() }
            emit(Resource.Success(contacts))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "Something Went Wrong!"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach servers. Check your internet connection!"))
        }
    }
}