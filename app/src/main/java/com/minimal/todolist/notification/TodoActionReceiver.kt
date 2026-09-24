package com.minimal.todolist.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.minimal.todolist.TodoApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles the circular "complete" button tap coming directly from a
 * notification, without needing to open the app.
 */
class TodoActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_COMPLETE = "com.minimal.todolist.ACTION_COMPLETE"
        const val EXTRA_ID = "extra_todo_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_COMPLETE) return
        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id == -1) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = (context.applicationContext as TodoApplication).repository
                repository.complete(id)
                NotificationHelper.cancel(context, id)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
