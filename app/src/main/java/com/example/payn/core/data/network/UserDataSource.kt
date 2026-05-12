package com.example.payn.core.data.network

import com.example.payn.core.config.AppConfig
import com.example.payn.core.data.dto.UpdateUserFormDTO
import com.example.payn.core.data.dto.UserDTO
import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.ApiResponse
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

private const val BASE_URL = "${AppConfig.BASE_API_URL}/users"

class UserDataSource(private val httpClient: HttpClient) {
    suspend fun getCurrentUser(): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/"
            )
        }
    }

    suspend fun getUserById(userId: String): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return safeCall {
            httpClient.get(
                urlString = "$BASE_URL/$userId"
            )
        }
    }

    suspend fun updateCurrentUser(
        updateUserFormDTO: UpdateUserFormDTO,
        profileImageBytes: ByteArray? = null
    ): Result<ApiResponse<UserDTO>, DataError.Remote> {
        return safeCall {
            httpClient.patch(
                urlString = "$BASE_URL/"
            ) {
                if (profileImageBytes != null) {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                updateUserFormDTO.username?.let { append("username", it) }
                                updateUserFormDTO.firstname?.let { append("firstname", it) }
                                updateUserFormDTO.lastname?.let { append("lastname", it) }

                                append("profile_image", profileImageBytes, Headers.build {
                                    append(HttpHeaders.ContentDisposition, "filename=\"profile_image.jpg\"")
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                })
                            }
                        )
                    )
                } else {
                    contentType(ContentType.Application.Json)
                    setBody(updateUserFormDTO)
                }
            }
        }
    }
}