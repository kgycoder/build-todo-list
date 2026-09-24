package com.minimal.todolist.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.minimal.todolist.MainActivity
import com.minimal.todolist.R
import com.minimal.todolist.data.Todo

/**
 * Builds and manages the persistent, per-todo notifications.
 * Each active (not completed) todo gets exactly one notification with a
 * circular "complete" button that mirrors the in-app design.
 */
object NotificationHelper {

    const val CHANNEL_ID = "todo_channel"
    private const val CHANNEL_NAME = "Todo 알림"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "진행 중인 Todo 알림"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun hasPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun notify(context: Context, todo: Todo) {
        if (todo.isDone) {
            cancel(context, todo.id)
            return
        }
        if (!hasPermission(context)) return

        val remoteViews = RemoteViews(context.packageName, R.layout.notification_todo)
        remoteViews.setTextViewText(R.id.notif_text, todo.text)
        remoteViews.setImageViewResource(R.id.notif_check_button, R.drawable.ic_circle_unchecked)

        val completeIntent = Intent(context, TodoActionReceiver::class.java).apply {
            action = TodoActionReceiver.ACTION_COMPLETE
            putExtra(TodoActionReceiver.EXTRA_ID, todo.id)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews.setOnClickPendingIntent(R.id.notif_check_button, completePendingIntent)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            10_000 + todo.id,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)

        NotificationManagerCompat.from(context).apply {
            if (hasPermission(context)) {
                notify(todo.id, builder.build())
            }
        }
    }

    fun cancel(context: Context, id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

    /** Sync all notifications to match the current todo list state. */
    fun refreshAll(context: Context, todos: List<Todo>) {
        todos.forEach { todo -> notify(context, todo) }
    }
}
