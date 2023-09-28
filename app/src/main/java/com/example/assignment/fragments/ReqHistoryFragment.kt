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
import com.example.assignment.FoodR
import com.example.assignment.HistoryAdapter
import com.example.assignment.HomeReqRecyclerAdapter
import com.example.assignment.R
import com.example.assignment.ReqHistoryAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReqHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var reqFoodArrayList: ArrayList<FoodR>
    private lateinit var historyAdapter: ReqHistoryAdapter
    private lateinit var db: FirebaseFirestore
    private var uri: Uri? = null // Initialize with null
    private var image: ImageView? = null // Initialize with null

    private val galleryImage = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback { result: Uri? ->
            result?.let {
                uri = it

                image?.setImageURI(result)
            }
        })

    private val galleryImageAdapt = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback { result: Uri? ->
            result?.let {
                uri = it

                historyAdapter.updateImageUri(result)
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_req_history, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerViewReq)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        reqFoodArrayList = arrayListOf()

        historyAdapter = ReqHistoryAdapter(reqFoodArrayList,galleryImageAdapt)

        recyclerView.adapter = historyAdapter

        // Pass the user ID to the EventChangeListener function here
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            EventChangeListener(userId)
        }


        return rootView

    }

    private fun EventChangeListener(userId: String){

        db = FirebaseFirestore.getInstance()
        db.collection("foodR").whereEqualTo("userId", userId) // Filter by user ID
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error!= null) {
                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    // Clear the existing list to avoid duplicates
                    reqFoodArrayList.clear()

                    // Iterate through the documents and add matching items to the list
                    for (document in value!!.documents) {
                        val foodData = document.toObject(FoodR::class.java)
                        if (foodData != null) {
                            // Get the document ID
                            val foodId = document.id

                            // Add the document ID along with other data to the list
                            foodData.id = foodId
                            reqFoodArrayList.add(foodData)
                        }
                    }
                    // Inside EventChangeListener
                    Log.d("ReqHistoryFragment", "Firestore data detected")

                    historyAdapter.notifyDataSetChanged()
                }
            })

    }




    private fun updateFood(upatedFood: Food){

    }

}