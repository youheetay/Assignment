package com.example.assignment.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.FoodR
import com.example.assignment.Adapter.HomeReqRecyclerAdapter
import com.example.assignment.R
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.Locale

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

//        // Find the SearchView inside the included layout
//        val includedLayout = rootView.findViewById<View>(R.id.include)
//        val searchView = includedLayout.findViewById<SearchView>(R.id.searchView)
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                filterList(newText)
//                return true
//            }
//
//        })

        foodReqArrayList = arrayListOf()


        homeReqRecyclerAdapter = HomeReqRecyclerAdapter(requireContext(),foodReqArrayList,requireContext())

        recyclerViewReq.adapter = homeReqRecyclerAdapter

        EventChangeListener()

        return rootView
    }

    private fun filterList(query : String?){
        if(query != null){
            val filteredList = ArrayList<FoodR>()
            for (i in foodReqArrayList){
                if(i.foodNameR?.toLowerCase(Locale.ROOT)!!.contains(query)){
                    filteredList.add(i)
                }
            }
            if(filteredList.isEmpty()){
                Toast.makeText(context,"No Data Found", Toast.LENGTH_SHORT).show()
            }else{
                homeReqRecyclerAdapter.setFilteredList(filteredList)
            }
        }
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
