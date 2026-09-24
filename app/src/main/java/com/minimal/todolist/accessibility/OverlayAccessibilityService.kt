package com.minimal.todolist.accessibility

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.minimal.todolist.TodoApplication
import com.minimal.todolist.notification.NotificationHelper
import com.minimal.todolist.ui.components.QuickAddOverlay
import com.minimal.todolist.ui.theme.TodoListTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Registers a soft "accessibility button" (the pill/icon that appears in the
 * navigation bar when this service is enabled). Tapping it draws a small
 * quick-add card as a system overlay directly on top of whatever screen the
 * user is currently in, without switching apps.
 */
class OverlayAccessibilityService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var overlayView: ComposeView? = null
    private var overlayLifecycleOwner: ServiceLifecycleOwner? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            accessibilityButtonController.registerAccessibilityButtonCallback(
                object : AccessibilityButtonController.AccessibilityButtonCallback() {
                    override fun onClicked(controller: AccessibilityButtonController) {
                        toggleOverlay()
                    }
                }
            )
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed: we only react to the accessibility button.
    }

    override fun onInterrupt() {
        removeOverlay()
    }

    private fun toggleOverlay() {
        if (overlayView != null) removeOverlay() else showOverlay()
    }

    private fun showOverlay() {
        val repository = (application as TodoApplication).repository

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        val owner = ServiceLifecycleOwner().also { overlayLifecycleOwner = it }
        owner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        owner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        owner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TodoListTheme {
                    QuickAddOverlay(
                        onDismiss = { removeOverlay() },
                        onAdd = { text ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val todo = repository.add(text)
                                NotificationHelper.notify(this@OverlayAccessibilityService, todo)
                            }
                            removeOverlay()
                        }
                    )
                }
            }
        }

        overlayView = composeView
        windowManager.addView(composeView, params)
    }

    private fun removeOverlay() {
        val view = overlayView ?: return
        try {
            windowManager.removeView(view)
        } catch (_: IllegalArgumentException) {
            // View was already detached; nothing to do.
        }
        overlayLifecycleOwner?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        overlayLifecycleOwner = null
        overlayView = null
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }
}
