package com.sendhur.contactsapp.presentation.screens.contact_detail.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sendhur.contactsapp.R
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.presentation.ContentText
import com.sendhur.contactsapp.presentation.screens.contact_detail.ContactDetailViewModel
import com.sendhur.contactsapp.presentation.screens.contact_detail.state.ContactDetailEvent
import com.sendhur.contactsapp.presentation.screens.contact_detail.state.UIState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeDetail(viewModel: ContactDetailViewModel, navController: NavController, onSaveContact: (Contact, Boolean) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (viewModel.isAddNewContact.value) {
                        ContentText(text = "Add new contact")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(
                                R.string.back
                            )
                        )
                    }
                },
            )
        },
        floatingActionButtonPosition = FabPosition.Start,
        floatingActionButton = {
            if (viewModel.isAddNewContact.value) {
                FloatingActionButton(onClick = {
                    viewModel.onEvent(ContactDetailEvent.AddNewContact)
                }, modifier = Modifier.padding(20.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = stringResource(id = R.string.save)
                    )
                }
            } else if (viewModel.isEditable.value) {
                FloatingActionButton(onClick = {
                    viewModel.onEvent(ContactDetailEvent.SaveContact)
                }, modifier = Modifier.padding(20.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Save,
                        contentDescription = stringResource(id = R.string.save)
                    )
                }
            } else {
                FloatingActionButton(onClick = {
                    viewModel.onEvent(ContactDetailEvent.EditClicked)
                }, modifier = Modifier.padding(20.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(id = R.string.edit)
                    )
                }
            }
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            viewModel.profileUrl.value.let {
                if (it.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(color = MaterialTheme.colorScheme.background)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.background,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = stringResource(id = R.string.profile_image),
                            modifier = Modifier
                                .size(120.dp)
                                .padding(10.dp)
                        )
                    }
                } else {
                    AsyncImage(
                        model = it,
                        contentDescription = stringResource(R.string.profile_image),
                        placeholder = painterResource(id = R.drawable.ic_person),
                        error = painterResource(id = R.drawable.ic_person),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(120.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val context = LocalContext.current
                LaunchedEffect(key1 = true) {
                    viewModel.evenFlow.collectLatest { event ->
                        when (event) {
                            is UIState.ShowError -> {
                                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                            }
                            is UIState.onEditClicked -> {
                                onSaveContact(event.contact, false)
                            }
                            is UIState.onSaveClicked -> {
                                onSaveContact(event.contact, true)
                            }
                        }
                    }
                }

                val nameState = viewModel.nameTextField.value
                val emailState = viewModel.emailTextField.value
                val phoneState = viewModel.phoneTextField.value

                OutlinedTextField(
                    value = nameState.text,
                    onValueChange = {
                        viewModel.onEvent(ContactDetailEvent.NameValueChanged(it))
                    },
                    label = { Text(text = nameState.hint) },
                    enabled = viewModel.isAddNewContact.value || viewModel.isEditable.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                OutlinedTextField(
                    value = phoneState.text,
                    onValueChange = {
                        viewModel.onEvent(ContactDetailEvent.PhoneValueChanged(it))
                    },
                    label = { Text(text = phoneState.hint) },
                    enabled = viewModel.isAddNewContact.value || viewModel.isEditable.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                OutlinedTextField(
                    value = emailState.text,
                    onValueChange = {
                        viewModel.onEvent(ContactDetailEvent.EmailValueChanged(it))
                    },
                    label = { Text(text = emailState.hint) },
                    enabled = viewModel.isAddNewContact.value || viewModel.isEditable.value,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
            }

        }

    }
}