package com.example.assignment.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminRequestFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("adminFoodReq")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_admin_request, container, false)

        rootView.findViewById<Button>(R.id.create).setOnClickListener{
            val textEnter = rootView.findViewById<EditText>(R.id.name).text.toString()

            val taskData = hashMapOf(
                "Name" to textEnter
            )

            collectionReference.add(taskData)
                .addOnSuccessListener { documentReference ->
                    //val taskId = documentReference.id
                    Toast.makeText(requireContext(), "Create Success", Toast.LENGTH_SHORT).show()

                    // Perform any additional actions or UI updates
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Toast.makeText(requireContext(), "Error adding task: ${e.message}", Toast.LENGTH_SHORT).show()

                }
        }

        return rootView
    }

}