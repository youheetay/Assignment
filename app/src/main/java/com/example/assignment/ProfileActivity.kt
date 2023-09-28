package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.assignment.databinding.ActivityLoginBinding
import com.example.assignment.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var uid : String
    private lateinit var historyBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        historyBtn = findViewById(R.id.historyBtn)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.buttonNavigationView)

        if (uid.isNotEmpty()) {
            getUserData()
        }


        binding.logoutBtn.setOnClickListener{
            auth.signOut()
            startActivity( Intent(this,LoginActivity::class.java))
        }

        binding.historyBtn.setOnClickListener{
            val historyIntent = Intent(this, HistoryViewActivity::class.java)
            startActivity(historyIntent)
        }

        // Set up a listener for item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }

                R.id.cart -> {
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }

                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }

                else -> false // Return false for items that are not handled
            }
        }
    }

    private fun getUserData() {
        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("user").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {

                        val user = document.toObject(User::class.java)
                        Glide.with(this) // Use 'this' for the activity context
                            .load(user?.image) // Use the image URL from the User object
                            .override(300, 300) // Set a fixed size of 300x300 pixels
                            .into(binding.imageView)

                        if (user != null) {
                            binding.userId.text = user.userId
                            binding.userName.text = user.userName
                            binding.gender.text = user.gender
                            binding.dob.text = user.DOB
                            binding.address.text = user.address

                        }

                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
}