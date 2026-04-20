package com.example.payn.contact.presentation.contact_list

import com.example.payn.contact.domain.Contact

data class ListContactsState(
    var searchQuery: String = "",
    var contacts: List<Contact> = emptyList()
)
