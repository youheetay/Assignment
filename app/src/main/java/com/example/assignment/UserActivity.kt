package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.assignment.databinding.ActivityUsersBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.donorButton.setOnClickListener{
            val donarIntent = Intent(this, DonarActivity::class.java)
            startActivity(donarIntent)
        }

        binding.requesterButton.setOnClickListener{
            val requesterIntent = Intent(this,RequesterActivity::class.java)
            startActivity(requesterIntent)
        }

        binding.backButton.setOnClickListener {
            onBackPressed() // Call onBackPressed to navigate back
        }
    }
}