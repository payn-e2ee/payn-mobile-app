package com.example.payn.contact.data.mappers

import com.example.payn.contact.data.dto.ContactDTO
import com.example.payn.contact.domain.Contact
import com.example.payn.core.data.mappers.toUser

fun ContactDTO.toContact(): Contact {
    return Contact(
        id = id,
        userId = userId,
        firstname = firstname,
        lastname = lastname,
        contactUserId = contactUserId,
        createdAt = createdAt,
        contactUser = contactUser?.toUser(),
    )
}