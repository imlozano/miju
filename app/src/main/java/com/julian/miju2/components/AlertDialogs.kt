package com.julian.miju2.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.julian.miju2.R

@Composable
fun ShowLoadingAlertDialog() {
    AlertDialog(
        onDismissRequest = { },
        title = { Text(stringResource(id = R.string.text_loading)) },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        },
        confirmButton = { }
    )
}

@Composable
fun ShowMessageAlertDialog(
    onConfirmation: () -> Unit,
    dialogTitle: Int,
    dialogText: Int
) {
    AlertDialog(
        title = { Text(text = stringResource(id = dialogTitle)) },
        text = { Text(text = stringResource(id = dialogText)) },
        onDismissRequest = { },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = R.string.btn_accept))
            }
        }
    )
}
