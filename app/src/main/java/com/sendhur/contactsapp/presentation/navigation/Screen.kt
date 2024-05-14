package com.sendhur.contactsapp.presentation.navigation

sealed class Screen(val route: String) {
    data object ContactsScreen: Screen("contacts")
    data object ContactDetailScreen: Screen("contact_detail")
}