package com.sendhur.contactsapp.presentation.screens.contacts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.sendhur.contactsapp.data.local.ContactEntity
import com.sendhur.contactsapp.data.mapper.toContact
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.presentation.screens.contacts.components.PhoneContactsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    pager: Pager<Int, ContactEntity>
) : ViewModel() {

    private val _phoneContactsState = mutableStateOf(PhoneContactsState())
    val phoneContactsState: State<PhoneContactsState> = _phoneContactsState

    val contactPagingFlow = pager.flow.map { pagingData ->
        pagingData.map { it.toContact() }
    }.cachedIn(viewModelScope)

    fun updatePhoneContacts(contacts: List<Contact>) {
        _phoneContactsState.value = PhoneContactsState(phoneContacts = contacts)
    }

    fun updatePhoneContactsError(error: String) {
        _phoneContactsState.value = PhoneContactsState(phoneContactsError = error)
    }
}