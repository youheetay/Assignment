package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.assignment.databinding.ActivityLoginBinding
import com.example.assignment.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var historyBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyBtn = findViewById(R.id.historyBtn)

        binding.historyBtn.setOnClickListener{
            val historyIntent = Intent(this, HistoryActivity::class.java)
            startActivity(historyIntent)
        }
    }

}