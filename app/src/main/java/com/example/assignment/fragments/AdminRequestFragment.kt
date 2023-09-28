package com.example.assignment.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Adapter.AdminReqAdapter
import com.example.assignment.Food
import com.example.assignment.FoodR
import com.example.assignment.R
import com.example.assignment.emptyActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AdminRequestFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var foodArrayList: ArrayList<FoodR>
    private lateinit var AdminReqAdapter : AdminReqAdapter
    //private lateinit var db: FirebaseFirestore
    private val db = FirebaseFirestore.getInstance()
    private var totalCount : Int = 0
    private lateinit var textView: TextView
    private var storageRef = Firebase.storage
    private var uri: Uri? = null // Initialize with null
    private var image: ImageView? = null // Initialize with null

    private val STORAGE_PERMISSION_CODE = 101 // Request code for storage permission

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

                AdminReqAdapter.updateImageUri(result)
            }
        })
    private val collectionReference = db.collection("foodR")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_admin_request, container, false)

        recyclerView = rootView.findViewById(R.id.ReqrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        foodArrayList = arrayListOf()

        AdminReqAdapter = AdminReqAdapter(foodArrayList,galleryImageAdapt)

        recyclerView.adapter = AdminReqAdapter

        textView = rootView.findViewById(R.id.notificationCount)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle back button behavior here
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        val collectionRef : CollectionReference = db.collection("foodPendingReq")
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
            val intent = Intent(activity, emptyActivity::class.java)
            startActivity(intent)
        }

        rootView.findViewById<FloatingActionButton>(R.id.CreateButton).setOnClickListener{
            addInfo()
        }


        return rootView
    }


    private fun addInfo() {
        val inflater = LayoutInflater.from(requireContext())
        val v = inflater.inflate(R.layout.admin_add_req_card, null)

        val addDialog = AlertDialog.Builder(requireContext())
            .setView(v)
            .create()

        val currentUser = FirebaseAuth.getInstance().currentUser

        image = v.findViewById(R.id.imageView)
        val textView2 = v.findViewById<TextView>(R.id.textView2)
        var quantity: NumberPicker = v.findViewById(R.id.foodNumReq)
        quantity.maxValue = 60
        quantity.minValue = 1
        quantity.wrapSelectorWheel = true
        quantity.setOnValueChangedListener { _, _, newValue ->
            textView2.text = "Quantity : $newValue"
        }

        addDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { dialog, _ ->
            val foodName = v.findViewById<EditText>(R.id.foodNameReq).text.toString()
            val foodDes = v.findViewById<EditText>(R.id.foodDesReq).text.toString()
            val selectedImageUri = uri // Get the selected image URI
            val quantity = quantity.value


            if (validateInput(foodName, foodDes, selectedImageUri, quantity)){
                if (currentUser != null) {
                    val userId = currentUser.uid

                    if (selectedImageUri != null) {

                        // Upload the selected image to Firebase Storage
                        val storageRef = storageRef.getReference("images").child(System.currentTimeMillis().toString())
                        storageRef.putFile(selectedImageUri)
                            .addOnSuccessListener { task ->
                                task.metadata?.reference?.downloadUrl
                                    ?.addOnSuccessListener { downloadUri ->
                                        // Create a new Food object with the downloaded image URL
                                        val food = FoodR(
                                            id = task.metadata?.name,
                                            foodNameR = foodName,
                                            foodDesR = foodDes,
                                            userId = userId,
                                            image = downloadUri.toString(),
                                            quantity = quantity
                                        )

                                        // Store the Food object in Firestore
                                        db.collection("foodR").document(task.metadata?.name ?: "")
                                            .set(food)
                                            .addOnSuccessListener {
                                                Toast.makeText(requireContext(), "Upload Successful", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener { error ->
                                                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
                                            }
                                    }
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(requireContext(), "Upload failed: $error", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Handle the case where no image was selected
                        Toast.makeText(requireContext(), "Please upload an image of food", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle the case where the user is not signed in
                    Toast.makeText(requireContext(), "User not signed in", Toast.LENGTH_SHORT).show()
                }
            }


            dialog.dismiss()
        }

        addDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        v.findViewById<Button>(R.id.browseBtn).setOnClickListener {
            // Launch the image picker after requesting permission
            galleryImage.launch("image/*")
        }
        image?.setImageResource(R.drawable.baseline_image_24)

        // Resize the ImageView
        val newWidthInPixels = 300 // Adjust this value as needed
        val newHeightInPixels = 300 // Adjust this value as needed
        val layoutParams = image?.layoutParams
        layoutParams?.width = newWidthInPixels
        layoutParams?.height = newHeightInPixels
        image?.layoutParams = layoutParams


        addDialog.show()
    }

    private fun validateInput(
        foodNameR: String,
        foodDesR: String,
        selectedImageUri: Uri?,
        quantity: Int
    ): Boolean {
        if (foodNameR.isEmpty() || foodDesR.isEmpty()) {
            // Show an error message for empty fields
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedImageUri == null) {
            // Show an error message for missing image
            Toast.makeText(requireContext(), "Please upload an image of food", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        if (quantity < 1 || quantity > 60) {
            // Show an error message for quantity outside the valid range
            Toast.makeText(
                requireContext(),
                "Quantity must be between 1 and 60",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Additional validation logic can be added here

        return true // All validation checks passed
    }

    private fun EventChangeListener(userId: String){

        //db = FirebaseFirestore.getInstance()
        db.collection("foodR").addSnapshotListener(object : EventListener<QuerySnapshot> {
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

                AdminReqAdapter.notifyDataSetChanged()
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