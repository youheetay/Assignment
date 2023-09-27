package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import com.example.assignment.databinding.ActivityLoginBinding
import com.example.assignment.databinding.ActivityProfileSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileSetup1 : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.saveProfileBtn.setOnClickListener {
            val name = binding.profileName.text.toString()
            val gender = binding.profileGender.text.toString()
            val dob = binding.dateOfBirth.text.toString()
            val address = binding.profileAddress.text.toString()

            if (name.isNotEmpty() && gender.isNotEmpty() && dob.isNotEmpty() && address.isNotEmpty()) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val userId = currentUser.uid

                    val userMap = hashMapOf(
                        "userName" to name,
                        "gender" to gender,
                        "dob" to dob,
                        "address" to address,
                        "userId" to userId
                    )

                    // Reference a new document with a generated ID
                    db.collection("user").add(userMap)
                        .addOnSuccessListener {
//                            Toast.makeText(this, "Successfully save profile!", Toast.LENGTH_SHORT)
//                                .show()

                            val intent = Intent(this, UserActivity::class.java)

                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed To save profile!", Toast.LENGTH_SHORT)
                                .show()
                            // Finish the current activity to go back to the previous page
                            finish()
                        }
                } else {
                    // Handle the case where the user is not signed in
                    Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
                }
            }

            val cancelBtn = findViewById<Button>(R.id.backProfileBtn)
            cancelBtn.setOnClickListener {
                val intent = Intent(this, UserActivity::class.java)

                startActivity(intent)
            }


        }
    }
}