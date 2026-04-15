package com.example.payn.profile.data.repository

import com.example.payn.core.domain.DataError
import com.example.payn.core.domain.Result
import com.example.payn.profile.data.network.ProfileDataSource
import com.example.payn.profile.domain.Profile

class ProfileRepository(
    private val profileDataSource: ProfileDataSource,
) {
    suspend fun getProfileById(profileId: String): Result<Profile, DataError> {
        return profileDataSource.getProfileById(profileId)
    }
}