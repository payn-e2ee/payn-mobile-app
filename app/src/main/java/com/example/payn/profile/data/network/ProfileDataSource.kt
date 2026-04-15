package com.example.payn.profile.data.network

import com.example.payn.core.data.safeCall
import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import com.example.payn.profile.domain.Profile
import io.ktor.client.HttpClient
import io.ktor.client.request.get

private const val BASE_URL = "https://openlibrary.org"

class ProfileDataSource(private val httpClient: HttpClient) {
    suspend fun getProfileById(profileId: String): Result<Profile, DataError.Remote> {
        return safeCall<Profile> {
            httpClient.get(
                urlString = "$BASE_URL/api/users/$profileId"
            )
        }
    }
}
