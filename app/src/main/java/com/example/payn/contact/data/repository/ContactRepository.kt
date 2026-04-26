package com.example.payn.contact.data.repository

import com.example.payn.contact.data.dto.ContactDTO
import com.example.payn.contact.data.network.ContactDataSource
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import com.example.payn.contact.data.dto.CreateContactDTO

class ContactRepository(
    private val contactDataSource: ContactDataSource,
) {
    suspend fun listContacts(): Result<ApiResponse<List<ContactDTO>>, DataError> {
        return contactDataSource.listContacts()
    }

    suspend fun getContactById(contactId: String): Result<ApiResponse<ContactDTO>, DataError> {
        return contactDataSource.getContactById(contactId)
    }

    suspend fun createContact(createContactDTO: CreateContactDTO): Result<ApiResponse<ContactDTO>, DataError> {
        return contactDataSource.createContact(createContactDTO)
    }

    suspend fun deleteContact(contactId: String): Result<ApiResponse<Unit>, DataError> {
        return contactDataSource.deleteContact(contactId)
    }
}