package com.example.payn.contact.presentation.contact_list

import com.example.payn.contact.domain.Contact

data class ListContactsState(
    val searchQuery: String = "",
    val contacts: List<Contact> = emptyList(),
    val isAddContactModalOpen: Boolean = false,
    val newContactFirstName: String = "",
    val newContactLastName: String = "",
    val newContactPhoneNumber: String = "",
    val isAddingContact: Boolean = false,
    val addContactError: String? = null
)
