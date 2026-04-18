package com.example.payn.chat.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.White30

@Preview
@Composable
fun SearchInputPreview() {
    SearchInput(
        value = "",
        onValueChange = {}
    )
}

@Composable
fun SearchInput(
    value: String,
    onValueChange: (value: String) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.padding(12.dp)) {
            TextField(
                shape = RoundedCornerShape(16.dp),
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "Search messages...",
                        color = Gray600
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = White30
                )
            )
        }
    }
}