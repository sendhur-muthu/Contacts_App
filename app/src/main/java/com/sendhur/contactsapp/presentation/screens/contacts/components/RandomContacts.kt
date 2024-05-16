package com.sendhur.contactsapp.presentation.screens.contacts.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.presentation.ErrorMessage
import com.sendhur.contactsapp.presentation.PaginationLoader

@Composable
fun ApiContacts(contacts: LazyPagingItems<Contact>, onContactClicked: (Int) -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(key1 = contacts.loadState) {
        if (contacts.loadState.refresh is LoadState.Error) {
            Toast.makeText(
                context,
                (contacts.loadState.refresh as LoadState.Error).error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (contacts.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(contacts.itemCount) {
                    val contact = contacts[it]
                    if (contact != null) {
                        ContactItem(contact = contact) { selectedContact ->
                            onContactClicked(selectedContact.id)
                        }
                    }
                }
                contacts.apply {
                    when {
                        loadState.append is LoadState.Loading -> {
                            item {
                                PaginationLoader(modifier = Modifier)
                            }
                        }
                        loadState.refresh is LoadState.Error -> {
                            item {
                                (loadState.refresh as LoadState.Error).error.localizedMessage?.let {
                                    ErrorMessage(message = it, modifier = Modifier.fillParentMaxSize()) {
                                        retry()
                                    }
                                }
                            }
                        }
                        loadState.append is LoadState.Error -> {
                            item {
                                (loadState.append as LoadState.Error).error.localizedMessage?.let {
                                    ErrorMessage(message = it) {
                                        retry()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}