package com.sendhur.contactsapp.presentation.screens.contacts.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.sendhur.contactsapp.R
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.presentation.ContentText
import com.sendhur.contactsapp.presentation.screens.contacts.state.PhoneContactsState

@Composable
fun PhoneContacts(
    state: PhoneContactsState,
    onContactClicked: (Int) -> Unit,
    onRequestPermissionClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.phoneContacts) { contact ->
                ContactItem(contact = contact) {
                    onContactClicked(it.id)
                }
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        if (state.phoneContactsError.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ContentText(text = state.phoneContactsError)
                ContentText(
                    text = stringResource(R.string.if_the_cta_is_not_working_you_might_ve_denied_permission_multiple_times_kindly_enable_permission_from_app_setting),
                    size = 14.sp
                )
                Button(onClick = {
                    onRequestPermissionClicked()
                }) {
                    Text(text = stringResource(R.string.allow_contacts_permission))
                }
            }
        }
    }
}