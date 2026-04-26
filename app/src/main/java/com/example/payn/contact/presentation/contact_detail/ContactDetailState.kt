package com.example.payn.contact.presentation.contact_detail

import com.example.payn.contact.domain.Contact

data class ContactDetailState(
    val contact: Contact? = null,
    val isDeleting: Boolean = false,
    val error: String? = null
)
