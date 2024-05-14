package com.sendhur.contactsapp.presentation.screens.contacts.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sendhur.contactsapp.R
import com.sendhur.contactsapp.domain.model.Contact
import com.sendhur.contactsapp.presentation.ContentText

@Composable
fun ContactItem(
    contact: Contact,
    onItemClick: (Contact) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onItemClick(contact)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = contact.picture,
            contentDescription = stringResource(R.string.profile_image),
            placeholder = painterResource(id = R.drawable.ic_person),
            error = painterResource(id = R.drawable.ic_person),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp),
        ) {
            ContentText(text = contact.name)
            Spacer(modifier = Modifier.size(4.dp))
            ContentText(
                text = contact.phone,
                style = MaterialTheme.typography.bodySmall,
                size = 14.sp
            )
        }
    }
}