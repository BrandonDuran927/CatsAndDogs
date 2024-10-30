package com.example.catsanddogs.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Confirmation(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onCancel()
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message
                )

                Button(
                    onClick = {
                        onCancel()
                    }
                ) {
                    Text(text = cancelText)
                }

                Button(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text(text = confirmText)
                }
            }
        },
        title = {
            Text(title)
        }
    )
}