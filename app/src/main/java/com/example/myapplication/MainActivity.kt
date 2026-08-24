package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/**
 * The entry point of the application.
 * This activity provides a dashboard for the user to select different chat modes.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Called when the activity is first created.
     * Initializes the UI and sets up click listeners for the dashboard buttons.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Each button opens ChatActivity with a different mode string
        // btnEmergency: For emergency knowledge mode
        findViewById<MaterialButton>(R.id.btnEmergency).setOnClickListener {
            openChat("emergency")
        }
        // btnPdf: For PDF reader mode
        findViewById<MaterialButton>(R.id.btnPdf).setOnClickListener {
            openChat("pdf")
        }
        // btnChat: For general chat mode
        findViewById<MaterialButton>(R.id.btnChat).setOnClickListener {
            openChat("chat")
        }
    }

    /**
     * Launches the ChatActivity with the specified mode.
     *
     * @param mode The operational mode for the chat (e.g., "emergency", "pdf", "chat").
     */
    private fun openChat(mode: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("mode", mode)
        startActivity(intent)
    }
}
