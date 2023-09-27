package com.example.assignment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore
        val foodCollection = db.collection("foodR")

        // Execute a query to get all documents in the "food" collection
        foodCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Access the data in each document
                    val foodName = document.getString("foodNameR")
                    val foodDescription = document.getString("foodDesR")
                    val quantity = document.getLong("quantity")
                    val requestedFood : TextView = findViewById<TextView>(R.id.requestedFood)
                    val quantityReq : TextView = findViewById<TextView>(R.id.quantityReq)
                    val descriptionReq : TextView = findViewById<TextView>(R.id.descriptionReq)
                    requestedFood.text = foodName
                    quantityReq.text = quantity.toString()
                    descriptionReq.text = foodDescription
                    // Do something with the retrieved data (e.g., display it in your app)
                    // You can also add this data to a list or adapter for display
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors, such as network issues or permission problems
                // You can log the error or display a message to the user
                // For example, Log.e(TAG, "Error getting documents: $exception")
            }

    }
}