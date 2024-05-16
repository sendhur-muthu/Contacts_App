package com.sendhur.contactsapp.presentation.screens.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.sendhur.contactsapp.R
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.presentation.ContentText
import com.sendhur.contactsapp.presentation.navigation.Screen
import com.sendhur.contactsapp.presentation.screens.contacts.components.ApiContacts
import com.sendhur.contactsapp.presentation.screens.contacts.components.ContactItem
import com.sendhur.contactsapp.presentation.screens.contacts.components.PhoneContacts
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val phoneContactsState = viewModel.phoneContactsState.value
    val searchState = viewModel.searchState.value
    val context = LocalContext.current
    val pagerState = rememberPagerState {
        2
    }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val tabItems =
        listOf(stringResource(R.string.random_contacts), stringResource(R.string.phone_contacts))
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                fetchContacts(context) {
                    viewModel.updatePhoneContacts(it)
                }
            } else {
                viewModel.updatePhoneContactsError(context.getString(R.string.permission_not_allowed))
            }
        }
    var text by remember {
        mutableStateOf("")
    }
    var active by remember {
        mutableStateOf(false)
    }
    var searchClicked by remember {
        mutableStateOf(false)
    }
    if (!active) {
        text = ""
        viewModel.refreshSearch()
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.ContactDetailScreen.route + "/0")
            }, modifier = Modifier.padding(20.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.add_contact)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp)
        ) {
            SearchBar(
                query = text,
                modifier = Modifier.fillMaxWidth(),
                onQueryChange = {
                    searchClicked = false
                    text = it
                },
                onSearch = {
                    viewModel.searchContacts(it)
                    searchClicked = true
                    keyboardController?.hide()
                },
                active = active,
                onActiveChange = {
                    if (!it) {
                        searchClicked = false
                    }
                    active = it
                },
                placeholder = {
                    ContentText(text = stringResource(id = R.string.search_contacts))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(id = R.string.search_contacts)
                    )
                },
                trailingIcon = {
                    if (active) {
                        Icon(
                            modifier = Modifier.clickable {
                                if (text.isNotEmpty()) {
                                    text = ""
                                } else {
                                    active = false
                                }
                                viewModel.refreshSearch()
                            },
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(id = R.string.clear)
                        )
                    }
                }
            ) {
                if (searchState.contacts.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(searchState.contacts) {
                            ContactItem(
                                contact = it,
                                showPhoneContactsIcon = true
                            ) { selectedContact ->
                                navigateToDetails(navController, selectedContact.id)
                            }
                        }
                    }
                } else if (text.isNotEmpty() && searchState.contacts.isEmpty() && searchClicked) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        ContentText(
                            text = stringResource(R.string.search_not_found),
                            modifier = Modifier.align(
                                Alignment.Center
                            )
                        )
                    }
                }
            }
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabItems.forEachIndexed { index, title ->
                    Tab(selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            ContentText(text = title)
                        }
                    )
                }
            }
            val contacts = viewModel.contactPagingFlow.collectAsLazyPagingItems()
            if (!viewModel.isPhoneContactsRetrieved) {
                requestPermission(context, viewModel, launcher)
            }
            HorizontalPager(state = pagerState, pageSpacing = 10.dp) {
                when (it) {
                    0 -> ApiContacts(contacts) { selectedContactId ->
                        navigateToDetails(navController, selectedContactId)
                    }

                    1 -> {
                        PhoneContacts(
                            state = phoneContactsState,
                            onContactClicked = { selectedContactId ->
                                navigateToDetails(navController, selectedContactId)
                            }) {
                            requestPermission(context, viewModel, launcher)
                        }
                    }
                }
            }
        }
    }
}

fun navigateToDetails(navController: NavController, id: Int) {
    navController.navigate(Screen.ContactDetailScreen.route + "/${id}")
}

fun requestPermission(
    context: Context,
    viewModel: ContactsViewModel,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fetchContacts(context) { contacts ->
            viewModel.updatePhoneContacts(contacts)
        }
    } else {
        launcher.launch(Manifest.permission.READ_CONTACTS)
    }
}

fun fetchContacts(context: Context, onContactsRetrieved: (List<Contact>) -> Unit) {
    val projections = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
        ContactsContract.Contacts.PHOTO_URI,
        ContactsContract.CommonDataKinds.Email.ADDRESS
    )
    val contentResolver = context.contentResolver
    val cursor = contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        projections,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )
    val list = mutableListOf<Contact>()
    cursor?.use {
        val phoneHashSet = HashSet<String>()
        val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
        val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val photoIndex = it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
        val emailIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
        while (it.moveToNext()) {
            val phoneNumber = it.getString(phoneNumberIndex).orEmpty().replace(" ", "")
            if (!phoneHashSet.contains(phoneNumber)) {
                phoneHashSet.add(phoneNumber)
                val contact = Contact(
                    email = it.getString(emailIndex).orEmpty(),
                    phone = phoneNumber,
                    picture = it.getString(photoIndex).orEmpty(),
                    name = it.getString(nameIndex).orEmpty(),
                    isPhoneContact = true
                )
                list.add(contact)
            }
        }
    }
    if (list.isNotEmpty()) {
        onContactsRetrieved(list)
    }
}
