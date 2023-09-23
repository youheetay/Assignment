package com.example.assignment.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Food
import com.example.assignment.HomeRecyclerAdapter
import com.example.assignment.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class DonorFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var foodArrayList: ArrayList<Food>
    private lateinit var homeRecyclerAdapter: HomeRecyclerAdapter
    private lateinit var db: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_donor, container, false)


        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        foodArrayList = arrayListOf()

        homeRecyclerAdapter = HomeRecyclerAdapter(foodArrayList)

        recyclerView.adapter = homeRecyclerAdapter

        EventChangeListener()

        return rootView
    }

    private fun EventChangeListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("food").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
               if (error!= null) {
                   Log.e("Firestore Error", error.message.toString())
                   return
               }
                //when success
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        foodArrayList.add(dc.document.toObject(Food::class.java))
                    }
                }

                homeRecyclerAdapter.notifyDataSetChanged()
            }
        })

   }
}