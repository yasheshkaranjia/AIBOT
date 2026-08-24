package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Each button opens ChatActivity with a different mode string
        findViewById<MaterialButton>(R.id.btnEmergency).setOnClickListener {
            openChat("emergency")
        }
        findViewById<MaterialButton>(R.id.btnPdf).setOnClickListener {
            openChat("pdf")
        }
        findViewById<MaterialButton>(R.id.btnChat).setOnClickListener {
            openChat("chat")
        }
    }

    private fun openChat(mode: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("mode", mode)
        startActivity(intent)
    }
}
