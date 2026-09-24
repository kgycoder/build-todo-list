package com.minimal.todolist.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.minimal.todolist.R
import com.minimal.todolist.ui.theme.IosGray
import com.minimal.todolist.ui.theme.IosLightGray
import com.minimal.todolist.ui.theme.IosScrimColor
import kotlinx.coroutines.delay

/**
 * Floating quick-add card shown as a system overlay when the accessibility
 * button is tapped. Dims the screen behind it and lets the user create a
 * Todo without leaving whatever app they were in.
 */
@Composable
fun QuickAddOverlay(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        visible = true
        delay(150)
        focusRequester.requestFocus()
        keyboard?.show()
    }

    fun dismissAnimated(after: () -> Unit) {
        visible = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (visible) IosScrimColor else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = 0.85f),
            exit = fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.85f)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* absorb clicks so the card doesn't dismiss itself */ },
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.overlay_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .focusRequester(focusRequester),
                        placeholder = { Text(stringResource(R.string.add_todo_hint), color = IosGray) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = IosLightGray,
                            unfocusedContainerColor = IosLightGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if (text.isNotBlank()) onAdd(text.trim())
                        })
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.overlay_cancel), color = IosGray)
                        }
                        Button(
                            onClick = { if (text.isNotBlank()) onAdd(text.trim()) },
                            modifier = Modifier.padding(start = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text(stringResource(R.string.overlay_add))
                        }
                    }
                }
            }
        }
    }
}
