package com.sendhur.contactsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sendhur.contactsapp.presentation.screens.contacts.ContactsScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.ContactsScreen.route) {
        composable(route = Screen.ContactsScreen.route) {
            ContactsScreen(navController = navController)
        }
    }
}