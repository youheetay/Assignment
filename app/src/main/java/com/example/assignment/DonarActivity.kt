package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.ProfileSyncState
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.example.assignment.databinding.ActivityDonarBinding
import com.example.assignment.ui.theme.AssignmentTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DonarActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private lateinit var binding: ActivityDonarBinding

    private lateinit var editFoodName: EditText
    private lateinit var editDes: EditText
    private lateinit var food: RadioButton
    private lateinit var drink: RadioButton
    private lateinit var submitBtn: Button
    private lateinit var cancelBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.buttonNavigationView)

        editFoodName = findViewById(R.id.editFoodName)
        editDes = findViewById(R.id.editDes)
        food = findViewById(R.id.food)
        drink = findViewById(R.id.drink)

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
                // Add more cases for other items if needed
                else -> false // Return false for items that are not handled
            }
        }

        binding.submitBtn.setOnClickListener {
            val editFoodName = editFoodName.text.toString()
            val editDes = editDes.text.toString()

            val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

            val selectedValue = when (radioGroup.checkedRadioButtonId) {
                R.id.food -> "FOOD"
                R.id.drink -> "DRINK"
                else -> "No selection"
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid

                val foodMap = hashMapOf(
                    "foodName" to editFoodName,
                    "foodDes" to editDes,
                    "foodOrDrink" to selectedValue, // Store the selected value, not the RadioGroup
                    "userId" to userId
                )

                // Reference a new document with a generated ID
                db.collection("food").add(foodMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully Create Food!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed To Create Food!", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Handle the case where the user is not signed in
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }
}