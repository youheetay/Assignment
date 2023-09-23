package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.ProfileSyncState
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
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
import com.example.assignment.databinding.ActivityRequesterBinding
import com.example.assignment.ui.theme.AssignmentTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RequesterActivity : AppCompatActivity() {

    private var db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requester)

//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.buttonNavigationView)

        val textView5 : TextView = findViewById(R.id.textView5)
        var editFoodNameR : TextView = findViewById(R.id.foodNameR)
        var editDesR : TextView = findViewById(R.id.descriptionR)
        val quantity : NumberPicker = findViewById(R.id.numberPicker)
        quantity.maxValue = 60
        quantity.minValue = 1
        quantity.wrapSelectorWheel = true
        quantity.setOnValueChangedListener { numberPicker, oldValue, newValue -> textView5.text = "Quantity : $newValue" }

//        // Set up a listener for item clicks
//        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.home -> {
//                    val intent = Intent(this, HomeActivity::class.java)
//                    startActivity(intent)
//                    true // Return true to indicate that the item click is handled
//                }
//                R.id.cart -> {
//                    val intent = Intent(this, CartActivity::class.java)
//                    startActivity(intent)
//                    true // Return true to indicate that the item click is handled
//                }
//                R.id.profile -> {
//                    val intent = Intent(this, ProfileActivity::class.java)
//                    startActivity(intent)
//                    true // Return true to indicate that the item click is handled
//                }
//                // Add more cases for other items if needed
//                else -> false // Return false for items that are not handled
//            }
//        }
        val buttonR = findViewById<Button>(R.id.buttonR)
        buttonR.setOnClickListener {
            val editFoodNameR = editFoodNameR.text.toString()
            val editDesR = editDesR.text.toString()
            val quantity = quantity.value

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                // Create a specific document ID (e.g., using the user's ID or a custom ID)
                val documentId = "requestFood1"  // Replace with your desired ID

                val requestMap = hashMapOf(
                    "foodNameR" to editFoodNameR,
                    "foodDesR" to editDesR,
                    "quantity" to quantity // Store the selected value, not the RadioGroup
                )

                // Reference a new document with a generated ID
                db.collection("foodR").document(documentId).set(requestMap)
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