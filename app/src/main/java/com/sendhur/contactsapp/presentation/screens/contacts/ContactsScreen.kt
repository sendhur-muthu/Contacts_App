package com.sendhur.contactsapp.presentation.screens.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.sendhur.contactsapp.R
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.presentation.ContentText
import com.sendhur.contactsapp.presentation.screens.contacts.components.ApiContacts
import com.sendhur.contactsapp.presentation.screens.contacts.components.PhoneContacts
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsScreen(
    navController: NavController,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val phoneContactsState = viewModel.phoneContactsState.value
    val context = LocalContext.current
    val pagerState = rememberPagerState {
        2
    }
    val coroutineScope = rememberCoroutineScope()
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
    Column(modifier = Modifier.fillMaxSize()) {
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
        HorizontalPager(state = pagerState, pageSpacing = 10.dp) {
            when (it) {
                0 -> ApiContacts(contacts, navController)
                1 -> {
                    requestPermission(context, viewModel, launcher)
                    PhoneContacts(state = phoneContactsState, onContactClicked = {

                    }) {
                        requestPermission(context, viewModel, launcher)
                    }
                }
            }
        }
    }
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
