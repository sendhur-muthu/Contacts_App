package com.sendhur.contactsapp.presentation.screens.contact_detail

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Context
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.os.RemoteException
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.presentation.isWideDisplay
import com.sendhur.contactsapp.presentation.screens.contact_detail.components.LandscapeDetail
import com.sendhur.contactsapp.presentation.screens.contact_detail.components.PortraitDetail

@Composable
fun ContactDetailScreen(
    viewModel: ContactDetailViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    var contactDetail: Contact? = null
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                addNewContact(contactDetail!!, context, navController)
            } else {
                Toast.makeText(context, "Contact stored in App database", Toast.LENGTH_SHORT).show()
                navController.navigateUp()
            }
        }
    Box(modifier = Modifier.fillMaxSize()) {
        if (isWideDisplay()) {
            LandscapeDetail(viewModel, navController) { contact, _ ->
                contactDetail = contact
                requestPermission(context, launcher, contact, navController)
            }
        } else {
            PortraitDetail(viewModel, navController = navController) { contact, _ ->
                contactDetail = contact
                requestPermission(context, launcher, contact, navController)
            }
        }
    }
}

fun requestPermission(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    contact: Contact,
    navController: NavController
) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        addNewContact(contact, context, navController)
    } else {
        launcher.launch(Manifest.permission.WRITE_CONTACTS)
    }
}

fun addNewContact(contact: Contact, context: Context, navController: NavController) {
    if (contact.isPhoneContact) {
        val contentProviderOperations = ArrayList<ContentProviderOperation>()

        contentProviderOperations.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            )
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        contentProviderOperations.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                )
                .withValue(
                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                    contact.name
                )
                .build()
        )

        contentProviderOperations.add(
            ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                )
                .withValue(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    contact.phone
                )
                .withValue(
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                )
                .build()


        )
        contentProviderOperations.add(
            ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                )
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.email)
                .withValue(
                    ContactsContract.CommonDataKinds.Email.TYPE,
                    ContactsContract.CommonDataKinds.Email.TYPE_HOME
                )
                .build()
        )

        try {
            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, contentProviderOperations)
            Toast.makeText(context, "Contact stored in Phone", Toast.LENGTH_SHORT).show()
            navController.navigateUp()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
            Toast.makeText(context, "Contact stored in App database", Toast.LENGTH_SHORT).show()
            navController.navigateUp()
        } catch (e: RemoteException) {
            e.printStackTrace()
            Toast.makeText(context, "Contact stored in App database", Toast.LENGTH_SHORT).show()
            navController.navigateUp()
        }
    } else {
        navController.navigateUp()
    }
}

