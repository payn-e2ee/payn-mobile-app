package com.example.payn.contact.data.repository

import com.example.payn.chat.data.dto.ChatDTO
import com.example.payn.chat.data.network.ChatDataSource
import com.example.payn.contact.data.dto.ContactDTO
import com.example.payn.contact.data.network.ContactDataSource
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result

class ContactRepository(
    private val contactDataSource: ContactDataSource,
) {
    suspend fun listContacts(): Result<ApiResponse<List<ContactDTO>>, DataError> {
        return contactDataSource.listContacts()
    }
}