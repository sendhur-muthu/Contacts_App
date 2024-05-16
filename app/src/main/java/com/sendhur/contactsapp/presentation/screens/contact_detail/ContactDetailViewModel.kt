package com.sendhur.contactsapp.presentation.screens.contact_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sendhur.contactsapp.common.Constants
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.domain.usecase.GetContactUseCase
import com.sendhur.contactsapp.domain.usecase.UpdateContactUseCase
import com.sendhur.contactsapp.presentation.screens.contact_detail.state.ContactDetailEvent
import com.sendhur.contactsapp.presentation.screens.contact_detail.state.TextFieldState
import com.sendhur.contactsapp.presentation.screens.contact_detail.state.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val getContactUseCase: GetContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val _nameTextField = mutableStateOf(
        TextFieldState(
            hint = "Name"
        )
    )
    val nameTextField: State<TextFieldState> = _nameTextField

    private val _phoneTextField = mutableStateOf(
        TextFieldState(
            hint = "Phone number"
        )
    )
    val phoneTextField: State<TextFieldState> = _phoneTextField

    private val _emailTextField = mutableStateOf(
        TextFieldState(
            hint = "Email"
        )
    )
    val emailTextField: State<TextFieldState> = _emailTextField

    private val _profileUrl = mutableStateOf("")
    val profileUrl: State<String> = _profileUrl

    private val _isEditable = mutableStateOf(false)
    val isEditable: State<Boolean> = _isEditable

    private val _eventFlow = MutableSharedFlow<UIState>()
    val evenFlow = _eventFlow.asSharedFlow()

    private var _isAddNewContact = mutableStateOf(false)
    val isAddNewContact: State<Boolean> = _isAddNewContact

    private var oldContact: Contact? = null

    init {
        savedStateHandle.get<String>(Constants.PARAM_CONTACT_ID)?.let {
            val contactId = it.toIntOrNull() ?: 0
            if (contactId > 0) {
                getContact(contactId)
            } else {
                _isAddNewContact.value = true
            }
        }
    }

    fun onEvent(event: ContactDetailEvent) {
        when (event) {
            is ContactDetailEvent.EmailValueChanged -> {
                _emailTextField.value = _emailTextField.value.copy(text = event.text)
            }

            is ContactDetailEvent.NameValueChanged -> {
                _nameTextField.value = _nameTextField.value.copy(text = event.text)
            }

            is ContactDetailEvent.PhoneValueChanged -> {
                _phoneTextField.value = _phoneTextField.value.copy(text = event.text)
            }

            is ContactDetailEvent.AddNewContact -> {
                if (_nameTextField.value.text.isNotBlank() && _phoneTextField.value.text.isNotBlank() && _emailTextField.value.text.isNotBlank()) {
                    val contact = Contact(
                        name = _nameTextField.value.text,
                        phone = _phoneTextField.value.text,
                        email = _emailTextField.value.text,
                        isPhoneContact = true
                    )
                    viewModelScope.launch {
                        updateContactUseCase(contact)
                        _eventFlow.emit(UIState.onSaveClicked(contact))
                    }

                } else {
                    viewModelScope.launch {
                        _eventFlow.emit(UIState.ShowError("Text fields shouldn't be empty!"))
                    }
                }
            }

            is ContactDetailEvent.SaveContact -> {
                if (_nameTextField.value.text.isNotBlank() && _phoneTextField.value.text.isNotBlank() && _emailTextField.value.text.isNotBlank()) {
                    //Save edited contact
                    val contact = oldContact?.copy(
                        name = _nameTextField.value.text,
                        phone = _phoneTextField.value.text,
                        email = _emailTextField.value.text
                    ) ?: Contact(
                        name = _nameTextField.value.text,
                        phone = _phoneTextField.value.text,
                        email = _emailTextField.value.text
                    )
                    viewModelScope.launch {
                        updateContactUseCase(contact)
                        _eventFlow.emit(UIState.onEditClicked(contact))
                    }
                } else {
                    viewModelScope.launch {
                        _eventFlow.emit(UIState.ShowError("Text fields shouldn't be empty!"))
                    }
                }
            }

            is ContactDetailEvent.EditClicked -> {
                _isEditable.value = true
            }
        }
    }

    private fun getContact(id: Int) {
        viewModelScope.launch {
            getContactUseCase(id)?.let {
                _nameTextField.value = _nameTextField.value.copy(text = it.name)
                _phoneTextField.value = _phoneTextField.value.copy(text = it.phone)
                _emailTextField.value = _emailTextField.value.copy(text = it.email)
                _profileUrl.value = it.picture
                oldContact = it
            }

        }
    }
}