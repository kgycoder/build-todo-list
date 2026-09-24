package com.minimal.todolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.minimal.todolist.R
import com.minimal.todolist.ui.theme.IosGray
import com.minimal.todolist.ui.theme.IosLightGray

@Composable
fun AddTodoBar(
    onAdd: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    fun submit() {
        if (text.isNotBlank()) {
            onAdd(text)
            text = ""
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    stringResource(R.string.add_todo_hint),
                    color = IosGray
                )
            },
            shape = RoundedCornerShape(22.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = IosLightGray,
                unfocusedContainerColor = IosLightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() })
        )

        Box(modifier = Modifier.padding(start = 8.dp)) {
            IconButton(
                onClick = { submit() },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (text.isNotBlank()) Color.Black else IosLightGray,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = "추가",
                    tint = if (text.isNotBlank()) Color.White else IosGray
                )
            }
        }
    }
}
