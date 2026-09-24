package com.minimal.todolist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.minimal.todolist.R
import com.minimal.todolist.data.Todo
import com.minimal.todolist.ui.TodoViewModel
import com.minimal.todolist.ui.components.AddTodoBar
import com.minimal.todolist.ui.components.TodoItem
import com.minimal.todolist.ui.theme.IosGray

@Composable
fun TodoListScreen(viewModel: TodoViewModel) {
    val todos by viewModel.todos.collectAsState()
    val active = todos.filter { !it.isDone }
    val done = todos.filter { it.isDone }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                AddTodoBar(onAdd = { viewModel.addTodo(it) })
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Todo",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
            )

            if (todos.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (active.isNotEmpty()) {
                        item(key = "section_active") {
                            SectionLabel(stringResource(R.string.section_active))
                        }
                        items(active, key = { it.id }) { todo ->
                            TodoRow(todo, viewModel)
                        }
                    }

                    if (done.isNotEmpty()) {
                        item(key = "section_done") {
                            SectionLabel(
                                stringResource(R.string.section_done),
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                        items(done, key = { it.id }) { todo ->
                            TodoRow(todo, viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoRow(todo: Todo, viewModel: TodoViewModel) {
    AnimatedVisibility(
        visible = true,
        exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
    ) {
        TodoItem(
            todo = todo,
            onToggle = {
                if (todo.isDone) viewModel.uncompleteTodo(todo) else viewModel.completeTodo(todo)
            },
            onDelete = { viewModel.deleteTodo(todo) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = IosGray,
        modifier = modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.empty_state_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.empty_state_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = IosGray,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
