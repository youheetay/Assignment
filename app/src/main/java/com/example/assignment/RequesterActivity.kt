package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment.databinding.ActivityDonarBinding
import com.example.assignment.databinding.ActivityRequesterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RequesterActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private lateinit var binding: ActivityRequesterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequesterBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_requester)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.buttonNavigationView1)

        val textView5 : TextView = findViewById(R.id.textView5)
        var editFoodNameR : TextView = findViewById(R.id.foodNameReq)
        var editDesR : TextView = findViewById(R.id.descriptionR)
        val quantity : NumberPicker = findViewById(R.id.numberPicker)
        quantity.maxValue = 60
        quantity.minValue = 1
        quantity.wrapSelectorWheel = true
        quantity.setOnValueChangedListener { numberPicker, oldValue, newValue -> textView5.text = "Quantity : $newValue" }

        // Set up a listener for item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }
                R.id.create -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }
                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }
                // Add more cases for other items if needed
                else -> false // Return false for items that are not handled
            }
        }
        val buttonR = findViewById<Button>(R.id.buttonR)
        buttonR.setOnClickListener {
            val editFoodNameR = editFoodNameR.text.toString()
            val editDesR = editDesR.text.toString()
            val quantity = quantity.value

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid

                val requestMap = hashMapOf(
                    "foodNameR" to editFoodNameR,
                    "foodDesR" to editDesR,
                    "quantity" to quantity,
                    "userId" to userId
                )

                // Reference a new document with a generated ID
                db.collection("foodPendingReq").add(requestMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Request Food!", Toast.LENGTH_SHORT).show()
                        // Finish the current activity to go back to the previous page
                        finish()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed To Request Food!", Toast.LENGTH_SHORT).show()
                        // Finish the current activity to go back to the previous page
                        finish()
                    }
            } else {
                // Handle the case where the user is not signed in
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
            }
        }

        val cancelR = findViewById<Button>(R.id.cancelR)
        cancelR.setOnClickListener {
            finish()
        }
    }
}