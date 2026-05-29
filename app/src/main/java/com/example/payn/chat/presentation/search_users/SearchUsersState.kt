package com.example.payn.chat.presentation.search_users

import com.example.payn.core.data.dto.SearchUserDTO

data class SearchUsersState(
    val query: String = "",
    val users: List<SearchUserDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val addingContactUserId: String? = null,
    val addContactError: String? = null,
)
