package com.example.payn.auth.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lucide
import com.example.payn.ui.theme.Gray700
import com.example.payn.ui.theme.Red500

// ✅ Type definition
enum class InputType {
    Text,
    Password
}

@Composable
fun Input(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    type: InputType = InputType.Text,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    errorMessage: String = ""
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val isPassword = type == InputType.Password

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            ),

            // 🔐 Password logic
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },

            keyboardOptions = if (isPassword) {
                KeyboardOptions(keyboardType = KeyboardType.Password)
            } else {
                keyboardOptions
            },

            // 👁️ Toggle icon (only for password)
            trailingIcon = {
                if (isPassword) {
                    val icon = if (passwordVisible) {
                        Lucide.Eye
                    } else {
                        Lucide.EyeOff
                    }

                    IconButton(onClick = {
                        passwordVisible = !passwordVisible
                    }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = if (passwordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            }
                        )
                    }
                }
            },
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

// ✅ Preview
@Preview(showBackground = true)
@Composable
fun InputPreview() {
    var text by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Input(
            label = "Username",
            value = text,
            onValueChange = { text = it },
            placeholder = "Type your username...",
            errorMessage = "Username field is required."
        )

        Input(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "Type your password...",
            type = InputType.Password,
            errorMessage = "Passwrod field is required."
        )
    }
}