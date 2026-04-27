package com.example.payn.contact.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import com.example.payn.core.presentation.components.GlassCard
import com.example.payn.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    onUpdateContact: () -> Unit,
    isUpdating: Boolean = false,
    error: String? = null
) {
    if (isOpen) {
        Dialog(
            onDismissRequest = onClose,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.4f) else White.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(24.dp)
                             )
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Edit Contact",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) White else Gray900
                            )
                            Icon(
                                Lucide.X,
                                contentDescription = "Close",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onClose() },
                                tint = if (isSystemInDarkTheme()) Gray400 else Gray600
                            )
                        }

                        // Form
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "First Name",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSystemInDarkTheme()) Gray400 else Gray600,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                CustomTextField(
                                    value = firstName,
                                    onValueChange = onFirstNameChange,
                                    placeholder = "John"
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Last Name",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSystemInDarkTheme()) Gray400 else Gray600,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                CustomTextField(
                                    value = lastName,
                                    onValueChange = onLastNameChange,
                                    placeholder = "Doe"
                                )
                            }
                        }

                        if (error != null) {
                            Text(
                                text = error,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onClose,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSystemInDarkTheme()) Gray900.copy(alpha = 0.5f) else Gray200.copy(alpha = 0.5f),
                                    contentColor = if (isSystemInDarkTheme()) White else Gray900
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Cancel", fontWeight = FontWeight.SemiBold, maxLines = 1)
                            }
                            Button(
                                onClick = onUpdateContact,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Blue500,
                                    contentColor = White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                enabled = !isUpdating && firstName.isNotBlank() && lastName.isNotBlank()
                            ) {
                                if (isUpdating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Save Changes", fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Gray500) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.3f) else White.copy(alpha = 0.5f),
            unfocusedContainerColor = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.3f) else White.copy(alpha = 0.5f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = if (isSystemInDarkTheme()) White else Gray900,
            unfocusedTextColor = if (isSystemInDarkTheme()) White else Gray900
        ),
        keyboardOptions = keyboardOptions,
        singleLine = true
    )
}
