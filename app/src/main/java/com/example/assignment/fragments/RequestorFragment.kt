package com.example.assignment.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.FoodR
import com.example.assignment.HomeReqRecyclerAdapter
import com.example.assignment.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class RequestorFragment : Fragment() {

    private lateinit var recyclerViewReq: RecyclerView
    private lateinit var foodReqArrayList: ArrayList<FoodR>
    private lateinit var homeReqRecyclerAdapter: HomeReqRecyclerAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_requestor, container, false)

        recyclerViewReq = rootView.findViewById(R.id.recyclerViewReq)
        recyclerViewReq.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewReq.setHasFixedSize(true)

        foodReqArrayList = arrayListOf()

        homeReqRecyclerAdapter = HomeReqRecyclerAdapter(requireContext(),foodReqArrayList,requireContext())

        recyclerViewReq.adapter = homeReqRecyclerAdapter

        EventChangeListener()

        return rootView
    }


    private fun EventChangeListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("foodR").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error!= null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                //when success
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        foodReqArrayList.add(dc.document.toObject(FoodR::class.java))
                    }
                }

                homeReqRecyclerAdapter.notifyDataSetChanged()
            }
        })

    }
}
