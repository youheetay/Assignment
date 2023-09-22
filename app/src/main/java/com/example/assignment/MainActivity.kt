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
        val requestButton : Button = findViewById(R.id.requesterButton)

        donarButton.setOnClickListener {
            val donarIntent = Intent(this, DonarActivity::class.java)
            startActivity(donarIntent)
        }

        requestButton.setOnClickListener{
            val requestIntent = Intent(this, RequesterActivity::class.java)
            startActivity(requestIntent)
        }

    }
}