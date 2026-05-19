package com.example.payn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.payn.app.App
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("PaynFCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d("PaynFCM", "Current token: $token")
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemUI()

        setContent {
            App()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)

        // Hide both bars
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        // Optional: allow swipe to show temporarily
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}
