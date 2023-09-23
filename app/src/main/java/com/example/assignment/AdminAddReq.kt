package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.firestore.FirebaseFirestore

class AdminAddReq : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("adminFoodReq")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_req)

        val create = findViewById<Button>(R.id.createBtn)

        create.setOnClickListener{
            val textEnter = findViewById<EditText>(R.id.name).text.toString()

            val taskData = hashMapOf(
                "Name" to textEnter
            )

            collectionReference.add(taskData)
                .addOnSuccessListener { documentReference ->
                    //val taskId = documentReference.id
                    Toast.makeText(this, "Create Success", Toast.LENGTH_SHORT).show()
                    finish()
                    // Perform any additional actions or UI updates
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Toast.makeText(this, "Error adding task: ${e.message}", Toast.LENGTH_SHORT).show()

                }
        }
    }
}