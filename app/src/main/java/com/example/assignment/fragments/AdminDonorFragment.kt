package com.example.assignment.fragments

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.AdminDonorAdapter
import com.example.assignment.Food
import com.example.assignment.R
import com.example.assignment.emptyActivityDonor
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class AdminDonorFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var foodArrayList: ArrayList<Food>
    private lateinit var AdminDonorAdapter : AdminDonorAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var textView: TextView
    private var totalCount : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_admin_donor, container, false)
        recyclerView = rootView.findViewById(R.id.DonorrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        foodArrayList = arrayListOf()

        AdminDonorAdapter = AdminDonorAdapter(foodArrayList)

        recyclerView.adapter = AdminDonorAdapter

        textView = rootView.findViewById(R.id.notificationCount)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        val collectionRef : CollectionReference = db.collection("foodPendingDonor")
        collectionRef.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    // Get the QuerySnapshot containing all documents in the collection
                    val querySnapshot = task.result

                    // Get the total count of documents
                    totalCount = querySnapshot.size()
                    textView.text = totalCount.toString()
                } else {
                    val exception = task.exception
                }
            }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            val userId = currentUser.uid
            EventChangeListener(userId)
        }

        rootView.findViewById<Button>(R.id.pendingButton).setOnClickListener{
            val intent = Intent(activity, emptyActivityDonor::class.java)
            startActivity(intent)
        }

        rootView.findViewById<FloatingActionButton>(R.id.CreateButton).setOnClickListener{
            addInfo()
        }


        return rootView
    }

    private fun addInfo(){

        val inflater = LayoutInflater.from(requireContext())
        val v = inflater.inflate(R.layout.admin_add_donor_card,null)

        val addDialog = AlertDialog.Builder(requireContext())
        addDialog.setView(v)

        val currentUser = FirebaseAuth.getInstance().currentUser

        val textView2 = v.findViewById<TextView>(R.id.textView2)
        var quantity : NumberPicker = v.findViewById(R.id.foodNumDonor)
        quantity.maxValue = 60
        quantity.minValue = 1
        quantity.wrapSelectorWheel = true
        quantity.setOnValueChangedListener { numberPicker, oldValue, newValue -> textView2.text = "Quantity : $newValue"
        }
        addDialog.setPositiveButton("Ok") { dialog, which ->

            val foodName = v.findViewById<EditText>(R.id.foodNameDonor).text.toString()
            val foodDes = v.findViewById<EditText>(R.id.foodDesDonor).text.toString()
            var quantity = quantity.value

            if (currentUser != null) {
                val userId = currentUser.uid

                val data = hashMapOf(
                    "foodName" to foodName,
                    "foodDes" to foodDes,
                    "quantity" to quantity,
                    "userId" to userId
                )

                // Add the data to Firestore
                db.collection("food").add(data)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Create Success", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        // Handle failure and log the error message
                        Log.e(ContentValues.TAG, "Error adding data: ${e.message}", e)
                        Toast.makeText(
                            requireContext(),
                            "Error adding data: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }else{
                // Handle the case where the user is not signed in
                Toast.makeText(requireContext(), "User not signed in", Toast.LENGTH_SHORT).show()
            }
        }


        addDialog.setNegativeButton("Cancel"){
                dialog, which ->
            dialog.dismiss()
            //Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
        }

        addDialog.create()
        addDialog.show()
    }

    private fun EventChangeListener(userId: String){

        //db = FirebaseFirestore.getInstance()
        db.collection("food").addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error!= null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                //when success
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){

                        val foodArray = dc.document.toObject(Food::class.java)
                        if (foodArray != null){
                            // Get the document ID
                            val foodDonorId = dc.document.id

                            // Add the document ID along with other data to the list
                            foodArray.id = foodDonorId
                            foodArrayList.add(foodArray)

                        }
                    }
                }

                AdminDonorAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        val collectionRef : CollectionReference = db.collection("foodPendingDonor")
        collectionRef.get()
            .addOnCompleteListener { task: Task<QuerySnapshot> ->
                if (task.isSuccessful) {
                    // Get the QuerySnapshot containing all documents in the collection
                    val querySnapshot = task.result

                    // Get the total count of documents
                    totalCount = querySnapshot.size()
                    if(totalCount >0){
                        textView.text = "Pending $totalCount record(s)"
                    }else{
                        textView.text = "No record(s)"
                    }

                } else {
                    val exception = task.exception
                }
            }

    }

}