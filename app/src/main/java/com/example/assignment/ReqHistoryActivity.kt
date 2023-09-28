//package com.example.assignment
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.EventListener
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.FirebaseFirestoreException
//import com.google.firebase.firestore.QuerySnapshot
//
//class ReqHistoryActivity : AppCompatActivity() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var reqFoodArrayList: ArrayList<FoodR>
//    private lateinit var historyAdapter: ReqHistoryAdapter
//    private lateinit var db: FirebaseFirestore
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_history)
//
//        recyclerView = findViewById(R.id.recyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.setHasFixedSize(true)
//
//        reqFoodArrayList = arrayListOf()
//
//        historyAdapter = ReqHistoryAdapter(reqFoodArrayList)
//
//        recyclerView.adapter = historyAdapter
//
//        // Pass the user ID to the EventChangeListener function here
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        if (currentUser != null) {
//            val userId = currentUser.uid
//            EventChangeListener(userId)
//        }
//    }
//
//    private fun EventChangeListener(userId: String){
//
//        db = FirebaseFirestore.getInstance()
//        db.collection("food").whereEqualTo("userId", userId) // Filter by user ID
//            .addSnapshotListener(object : EventListener<QuerySnapshot> {
//                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//                    if (error!= null) {
//                        Log.e("Firestore Error", error.message.toString())
//                        return
//                    }
//
//                    // Clear the existing list to avoid duplicates
//                    reqFoodArrayList.clear()
//
//                    // Iterate through the documents and add matching items to the list
//                    for (document in value!!.documents) {
//                        val reqFoodData = document.toObject(FoodR::class.java)
//                        if (reqFoodData != null) {
//                            // Get the document ID
//                            val foodId = document.id
//
//                            // Add the document ID along with other data to the list
//                            reqFoodData.userId = foodId
//                            reqFoodArrayList.add(reqFoodData)
//                        }
//                    }
//
//                    historyAdapter.notifyDataSetChanged()
//                }
//            })
//
//    }
//
//    private fun updateFood(upatedFood: Food){
//
//    }
//
//}
