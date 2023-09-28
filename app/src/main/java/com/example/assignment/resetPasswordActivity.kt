package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class resetPasswordActivity : AppCompatActivity() {

    private lateinit var setPassword: EditText
    private lateinit var btnResetPassword: Button

    private lateinit var auth: FirebaseAuth

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)


        setPassword = findViewById(R.id.setPassword)
        btnResetPassword =  findViewById(R.id.resetBtn)
        backButton = findViewById(R.id.backButton)

        auth = FirebaseAuth.getInstance()

        btnResetPassword.setOnClickListener{
            val password = setPassword.text.toString()
            auth.sendPasswordResetEmail(password)
                .addOnSuccessListener {
                    Toast.makeText(this,"Please Check Your Email",Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener{
                    Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
                }
        }

        backButton.setOnClickListener{
            finish()
        }

    }
}