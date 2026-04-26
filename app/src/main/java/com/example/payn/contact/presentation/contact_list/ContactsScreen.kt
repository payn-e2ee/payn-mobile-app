package com.example.payn.contact.presentation.contact_list

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.UserPlus
import com.example.payn.app.Route
import com.example.payn.contact.presentation.components.ContactItem
import com.example.payn.contact.presentation.components.SearchInput
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Blue400
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Blue600
import com.example.payn.ui.theme.Blue900
import com.example.payn.ui.theme.Gray400
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Pink400
import com.example.payn.ui.theme.Pink900
import com.example.payn.ui.theme.Purple400
import com.example.payn.ui.theme.Purple900
import com.example.payn.ui.theme.White
import com.example.payn.contact.presentation.components.AddContactModal

@Composable
fun ContactsScreen(
    viewModel: ListContactsViewModel,
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val backgroundGradient = if (isSystemInDarkTheme()) {
        Brush.linearGradient(listOf(Blue900, Purple900, Pink900))
    } else {
        Brush.linearGradient(listOf(Blue400, Purple400, Pink400))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(
                        "Contacts",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        "${state.contacts.size} contacts",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            }

            // Search Bar
            item {
                SearchInput(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) }
                )
            }

            // Add Contact Action
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { viewModel.toggleAddContactModal(true) }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Blue500.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Lucide.UserPlus,
                                contentDescription = null,
                                tint = if (isSystemInDarkTheme()) Blue400 else Blue600,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                "Add Contact",
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSystemInDarkTheme()) White else Gray900
                            )
                            Text(
                                "Add a contact by phone number",
                                fontSize = 14.sp,
                                color = if (isSystemInDarkTheme()) Gray400 else Gray600
                            )
                        }
                    }
                }
            }

            // Contacts List
            items(state.contacts, key = { it.id }) { contact ->
                ContactItem(
                    contact = contact,
                    onContactClick = { navController.navigate(Route.Contact(contact.id)) },
                    onChatClick = { }
                )
            }
        }

        AddContactModal(
            isOpen = state.isAddContactModalOpen,
            onClose = { viewModel.toggleAddContactModal(false) },
            firstName = state.newContactFirstName,
            onFirstNameChange = viewModel::onFirstNameChange,
            lastName = state.newContactLastName,
            onLastNameChange = viewModel::onLastNameChange,
            phoneNumber = state.newContactPhoneNumber,
            onPhoneNumberChange = viewModel::onPhoneNumberChange,
            onAddContact = viewModel::addContact,
            isAdding = state.isAddingContact,
            error = state.addContactError
        )
    }
}

