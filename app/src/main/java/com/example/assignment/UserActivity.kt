package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.assignment.databinding.ActivityUsersBinding

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.donarButton.setOnClickListener{
            val donarIntent = Intent(this, DonarActivity::class.java)
            startActivity(donarIntent)
        }
    }
}