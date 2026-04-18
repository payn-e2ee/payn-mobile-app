package com.example.payn.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.example.payn.ui.theme.White20

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(24.dp)

    val baseModifier = modifier
        .clip(shape)

    val clickableModifier = if (onClick != null) {
        baseModifier.clickable(onClick = onClick)
    } else {
        baseModifier
    }

    Box(modifier = clickableModifier) {

        // 🔹 Background blur layer (ONLY this is blurred)
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(20.dp) // backdrop blur effect
                .background(White.copy(alpha = 0.7f))
        )

        // 🔹 Border layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.dp,
                    color = White20,
                    shape = shape
                )
        )

        // 🔹 Content (NOT blurred)
        Box {
            content()
        }
    }
}
