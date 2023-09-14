package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        val donarButton: Button = findViewById(R.id.donarButton)

        donarButton.setOnClickListener {
            val donarIntent = Intent(this, DonarActivity::class.java)
            startActivity(donarIntent)
        }

    }
}