package com.example.payn.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.AtSign
import com.composables.icons.lucide.Lucide
import com.example.payn.ui.theme.Black30
import com.example.payn.ui.theme.Blue500
import com.example.payn.ui.theme.Gray500
import com.example.payn.ui.theme.Gray700
import com.example.payn.ui.theme.Gray900
import com.example.payn.ui.theme.Red500
import com.example.payn.ui.theme.White20
import com.example.payn.ui.theme.White50

@Composable
fun Input(
    modifier: Modifier? = Modifier,
    label: String,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    errorMessage: String = ""
) {
    Column(
        modifier = modifier ?: Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Gray700,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            isError = errorMessage.isNotEmpty(),
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = Red500,
                errorLabelColor = Red500,
                errorSupportingTextColor = Red500,
                errorContainerColor = White50,
                focusedBorderColor = Blue500.copy(alpha = 0.5f),
                unfocusedBorderColor = White20,
                focusedContainerColor = White50,
                unfocusedContainerColor = White50,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900,
            ),
            keyboardOptions = keyboardOptions,
            trailingIcon = if (trailingIcon != null) {
                {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = "trailing icon",
                        tint = Gray500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                null
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = "leading icon",
                        tint = Gray500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                null
            }
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                fontSize = 14.sp,
                color = Red500,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputPreview() {
    var text by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier.background(Black30)
    ) {
        Input(
            label = "Username",
            value = text,
            onValueChange = { text = it },
            placeholder = "Type your username...",
            errorMessage = "Username field is required.",
            leadingIcon = Lucide.AtSign,
        )

        Input(
            label = "Username",
            value = text,
            onValueChange = { text = it },
            placeholder = "Type your username...",
            leadingIcon = Lucide.AtSign,
        )
    }
}