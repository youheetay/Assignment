package com.example.assignment.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.AdminReqAdapter
import com.example.assignment.Adapter.AdminReqPendingAdapter
import com.example.assignment.AdminActivity
import com.example.assignment.Food
import com.example.assignment.FoodR
import com.example.assignment.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class AdminPendingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var foodArrayList: ArrayList<FoodR>
    private lateinit var AdminReqPendingAdapter : AdminReqPendingAdapter
    private val db = FirebaseFirestore.getInstance()
    private var totalCount : Int = 0
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_admin_pending, container, false)

        recyclerView = rootView.findViewById(R.id.pendingReqView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        foodArrayList = arrayListOf()

        AdminReqPendingAdapter = AdminReqPendingAdapter(foodArrayList)

        recyclerView.adapter = AdminReqPendingAdapter

        textView = rootView.findViewById(R.id.recordPending)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            val userId = currentUser.uid
            EventChangeListener(userId)
        }

        rootView.findViewById<Button>(R.id.backBtn).setOnClickListener {
            val intent = Intent(activity, AdminActivity::class.java)
            startActivity(intent)
        }

        return rootView
    }



    private fun EventChangeListener(userId: String){

        //val textView : TextView = findViewById(R.id.textView4)
        //db = FirebaseFirestore.getInstance()
        db.collection("foodPendingReq").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error!= null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                //when success
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){

                        val foodArray = dc.document.toObject(FoodR::class.java)
                        if (foodArray != null){
                            // Get the document ID
                            val foodReqId = dc.document.id

                            // Add the document ID along with other data to the list
                            foodArray.id = foodReqId
                            foodArrayList.add(foodArray)

                        }
                    }
                }

                AdminReqPendingAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        val collectionRef : CollectionReference = db.collection("foodPendingReq")
        collectionRef.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    // Get the QuerySnapshot containing all documents in the collection
                    val querySnapshot = task.result

                    // Get the total count of documents
                    totalCount = querySnapshot.size()
                    if(totalCount >0){
                        textView.text = "$totalCount record(s)"
                    }else{
                        textView.text = "No record(s)"
                    }

                } else {
                    val exception = task.exception
                }
            }

    }

}