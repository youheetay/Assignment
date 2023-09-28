package com.example.assignment.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Food
import com.example.assignment.Adapter.HistoryAdapter
import com.example.assignment.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class HistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var foodArrayList: ArrayList<Food>
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var db: FirebaseFirestore

    private var image: ImageView? = null
    private var uri: Uri? = null

    private val galleryImageAdapt = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback { result: Uri? ->
            result?.let {
                uri = it

                historyAdapter.updateImageUri(result)
            }
        })

    private val galleryImage = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback { result: Uri? ->
            result?.let {
                uri = it

                image?.setImageURI(result)
            }
        })


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        foodArrayList = arrayListOf()

        historyAdapter = HistoryAdapter(
            requireContext(),
            foodArrayList,
            galleryImageAdapt,
            parentFragmentManager
        )

        recyclerView.adapter = historyAdapter

        // Pass the user ID to the EventChangeListener function here
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            EventChangeListener(userId)
        }

        return rootView
    }

    private fun EventChangeListener(userId: String) {

        db = FirebaseFirestore.getInstance()
        db.collection("food").whereEqualTo("userId", userId) // Filter by user ID
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    // Clear the existing list to avoid duplicates
                    foodArrayList.clear()

                    // Iterate through the documents and add matching items to the list
                    for (document in value!!.documents) {
                        val foodData = document.toObject(Food::class.java)
                        if (foodData != null) {
                            // Get the document ID
                            val foodId = document.id

                            // Add the document ID along with other data to the list
                            foodData.id = foodId
                            foodArrayList.add(foodData)
                        }
                    }
                    // Inside EventChangeListener
                    Log.d("HistoryFragment", "Firestore data change detected")
                    // Inside EventChangeListener
                    Log.d("HistoryFragment", "Firestore data detected")

                    historyAdapter.notifyDataSetChanged()
                }
            })

    }


}