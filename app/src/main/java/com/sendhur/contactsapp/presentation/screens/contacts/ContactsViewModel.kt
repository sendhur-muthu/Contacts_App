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
import com.sendhur.contactsapp.domain.usecase.PhoneContactUseCase
import com.sendhur.contactsapp.domain.usecase.SearchContactsUseCase
import com.sendhur.contactsapp.presentation.screens.contacts.state.PhoneContactsState
import com.sendhur.contactsapp.presentation.screens.contacts.state.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    pager: Pager<Int, ContactEntity>,
    private val searchContactsUseCase: SearchContactsUseCase,
    private val phoneContactUseCase: PhoneContactUseCase
) : ViewModel() {

    private val _phoneContactsState = mutableStateOf(PhoneContactsState())
    val phoneContactsState: State<PhoneContactsState> = _phoneContactsState

    private val _searchState = mutableStateOf(SearchState())
    val searchState: State<SearchState> = _searchState

    val contactPagingFlow = pager.flow.map { pagingData ->
        pagingData.map { it.toContact() }
    }.cachedIn(viewModelScope)

    var isPhoneContactsRetrieved = false

    fun updatePhoneContacts(contacts: List<Contact>) {
        _phoneContactsState.value = PhoneContactsState(isLoading = true)
        viewModelScope.launch {
            phoneContactUseCase(contacts).let {
                isPhoneContactsRetrieved = true
                _phoneContactsState.value = PhoneContactsState(phoneContacts = it)
            }
        }
    }

    fun updatePhoneContactsError(error: String) {
        _phoneContactsState.value = PhoneContactsState(phoneContactsError = error)
    }

    fun searchContacts(query: String) {
        viewModelScope.launch {
            searchContactsUseCase("$query%").let {
                /*val list = mutableListOf<Contact>()
                list.addAll(phoneContactsState.value.phoneContacts.filter { contact ->
                    contact.phone.lowercase()
                        .startsWith(query.lowercase()) || contact.name.lowercase()
                        .startsWith(query.lowercase())
                })
                list.addAll(it)*/
                _searchState.value = SearchState(it)
            }
        }
    }

    fun refreshSearch() {
        _searchState.value = SearchState()
    }
}