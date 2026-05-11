package com.example.payn.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.File
import com.composables.icons.lucide.Download
import com.composables.icons.lucide.Lucide
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray600
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.White

@Composable
fun FileMessage(
    fileName: String,
    fileSize: String,
    isMe: Boolean,
    createdAt: String,
    onDownloadClick: () -> Unit
) {
    val containerColor = if (isMe) Blue500 else White.copy(alpha = 0.7f)
    val contentColor = if (isMe) White else Gray900
    val secondaryTextColor = if (isMe) White.copy(alpha = 0.7f) else Gray600

    Box(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .padding(12.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // File Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.File,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // File Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fileName,
                        color = contentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = fileSize,
                        color = secondaryTextColor,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Download Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.2f))
                        .clickable { onDownloadClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Lucide.Download,
                        contentDescription = "Download",
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Metadata
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatIsoDate(createdAt),
                    fontSize = 10.sp,
                    color = secondaryTextColor
                )

                if (isMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "✓✓",
                        fontSize = 10.sp,
                        color = secondaryTextColor
                    )
                }
            }
        }
    }
}