package com.example.payn.contact.data.network

import com.example.payn.contact.data.dto.ContactDTO
import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import com.example.payn.contact.data.dto.CreateContactDTO
import com.example.payn.contact.data.dto.UpdateContactDTO
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val BASE_URL = "${AppConfig.BASE_API_URL}/contacts"

class ContactDataSource(private val httpClient: HttpClient) {
    suspend fun listContacts(): Result<ApiResponse<List<ContactDTO>>, DataError.Remote> {
        return safeCall<ApiResponse<List<ContactDTO>>> {
            httpClient.get(
                urlString = "$BASE_URL/"
            )
        }
    }

    suspend fun getContactById(contactId: String): Result<ApiResponse<ContactDTO>, DataError.Remote> {
        return safeCall<ApiResponse<ContactDTO>> {
            httpClient.get(
                urlString = "$BASE_URL/$contactId"
            )
        }
    }

    suspend fun deleteContact(contactId: String): Result<ApiResponse<Unit>, DataError.Remote> {
        return safeCall<ApiResponse<Unit>> {
            httpClient.delete(
                urlString = "$BASE_URL/$contactId"
            )
        }
    }

    suspend fun createContact(createContactDTO: CreateContactDTO): Result<ApiResponse<ContactDTO>, DataError.Remote> {
        return safeCall<ApiResponse<ContactDTO>> {
            httpClient.post(
                urlString = "$BASE_URL"
            ) {
                contentType(ContentType.Application.Json)
                setBody(createContactDTO)
            }
        }
    }

    suspend fun updateContact(contactId: String, updateContactDTO: UpdateContactDTO): Result<ApiResponse<ContactDTO>, DataError.Remote> {
        return safeCall<ApiResponse<ContactDTO>> {
            httpClient.patch(
                urlString = "$BASE_URL/$contactId"
            ) {
                contentType(ContentType.Application.Json)
                setBody(updateContactDTO)
            }
        }
    }
}
