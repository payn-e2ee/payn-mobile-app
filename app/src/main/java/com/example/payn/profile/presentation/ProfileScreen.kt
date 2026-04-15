package com.example.payn.profile.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ProfileScreen(username: String = "") {
    Text(
        text = "Hello $username"
    )
}