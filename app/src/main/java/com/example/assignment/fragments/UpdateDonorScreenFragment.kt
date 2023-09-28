package com.example.assignment.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Food
import com.example.assignment.HistoryAdapter
import com.example.assignment.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class UpdateDonorScreenFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var foodArrayList: ArrayList<Food>
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_donor_screen, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        foodArrayList = arrayListOf()

       // historyAdapter = HistoryAdapter(requireContext(), foodArrayList, childFragmentManager)

        recyclerView.adapter = historyAdapter

        // Pass the user ID to the EventChangeListener function here
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            eventChangeListener(userId)
        }

        return view
    }

    private fun eventChangeListener(userId: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("food")
            .whereEqualTo("userId", userId) // Filter by user ID
            .addSnapshotListener(
                EventListener<QuerySnapshot> { value, error ->
                    if (error != null) {
                        // Handle error
                        return@EventListener
                    }

                    // Clear the existing list to avoid duplicates
                    foodArrayList.clear()

                    // Iterate through the documents and add matching items to the list
                    for (document in value!!) {
                        val foodData = document.toObject(Food::class.java)
                        val foodId = document.id

                        // Add the document ID along with other data to the list
                        foodData.id = foodId
                        foodArrayList.add(foodData)
                    }

                    historyAdapter.notifyDataSetChanged()
                })

    }

}