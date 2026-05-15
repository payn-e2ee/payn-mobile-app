package com.example.payn.chat.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CheckCheck
import com.composables.icons.lucide.Lucide
import com.example.payn.chat.domain.MessageStatus
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray300
import com.example.payn.ui.theme.White

@Composable
fun MessageStatus(status: MessageStatus, useBlue: Boolean = false) {
    when (status) {
        MessageStatus.SEEN -> Icon(
            imageVector = Lucide.CheckCheck,
            contentDescription = null,
            tint = if (useBlue) Blue500 else White,
            modifier = Modifier.size(14.dp)
        )

        MessageStatus.SENT -> Icon(
            imageVector = Lucide.Check,
            contentDescription = null,
            tint = if (useBlue) Blue500 else White,
            modifier = Modifier.size(14.dp)
        )

        MessageStatus.DELIVERED -> Icon(
            imageVector = Lucide.CheckCheck,
            contentDescription = null,
            tint = Gray300,
            modifier = Modifier.size(14.dp)
        )
    }
}