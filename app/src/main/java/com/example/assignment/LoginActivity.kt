package com.example.assignment

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.assignment.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Set an OnFocusChangeListener to each EditText
        binding.loginEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // If the EditText loses focus, hide the keyboard
                hideKeyboard()
            }
        }

        binding.loginPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // If the EditText loses focus, hide the keyboard
                hideKeyboard()
            }
        }

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (email.equals("admin@gmail.com") && password.equals("admin123")) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, AdminActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {

                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val userId = currentUser?.uid.toString()
                                db = FirebaseFirestore.getInstance()

                                db.collection("user").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists()) {
//                                            val userProfile = documentSnapshot.result?.toObject(User::class.java)
//                                            val firestoreUid = documentSnapshot.getString("userId")

                                            // User has a profile setup, redirect to HomeActivity
                                            val intent = Intent(this, HomeActivity::class.java)
                                            startActivity(intent)
                                        } else {
                                            // User doesn't have a profile setup, redirect to ProfileSetup1
                                            val intent = Intent(this, ProfileSetup1::class.java)
                                            startActivity(intent)
                                        }
                                    } .addOnFailureListener { e ->
                                        Toast.makeText(this, "Please Sign Up first", Toast.LENGTH_SHORT).show()
                                    }
                            }else{
                                Toast.makeText(this, "Password or Email is Invalid", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }else{
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }


        }

        binding.signupRedirectText.setOnClickListener{
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        currentFocusView?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}