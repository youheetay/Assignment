package com.example.assignment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.assignment.databinding.ActivityDonarBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DonarActivity : AppCompatActivity() {

    private var db = Firebase.firestore
    private lateinit var binding: ActivityDonarBinding

    private lateinit var editFoodName: EditText
    private lateinit var editDes: EditText

    private var image: ImageView? = null // Initialize with null
    private var uri: Uri? = null // Initialize with null
    private lateinit var browseBtn: Button
    private lateinit var quantityDonar: TextView


    private var storageRef = Firebase.storage
    private val STORAGE_PERMISSION_CODE = 101 // Request code for storage permission
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonarBinding.inflate(layoutInflater)
        setContentView(binding.root)

//
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        //setSupportActionBar(toolbar)
//
//        // Set the custom circular background for the Toolbar
//        toolbar.setBackgroundResource(R.drawable.rounded_toolbar_background)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.buttonNavigationView)

        editFoodName = findViewById(R.id.editFoodName)
        editDes = findViewById(R.id.editDes)
        browseBtn = findViewById(R.id.browseBtn)
        image = findViewById(R.id.imageView)
        quantityDonar = findViewById(R.id.quantityDonar)

        val quantity: NumberPicker = findViewById(R.id.selectQuantity)
        quantity.maxValue = 60
        quantity.minValue = 1
        quantity.wrapSelectorWheel = true
        quantity.setOnValueChangedListener { numberPicker,
                                             oldValue,
                                             newValue ->
            quantityDonar.text = "Quantity : $newValue"
        }


        // Set up a listener for item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }

                R.id.create -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }

                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }

                else -> false // Return false for items that are not handled
            }
        }

        binding.submitBtn.setOnClickListener {
            val editFoodName = editFoodName.text.toString()
            val editDes = editDes.text.toString()
            val quantity = quantity.value

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userId = currentUser.uid

                if (uri != null) {
                    // Upload the image first
                    val storageRef = storageRef.getReference("images")
                        .child(System.currentTimeMillis().toString())
                    storageRef.putFile(uri!!)
                        .addOnSuccessListener { task ->
                            task.metadata?.reference?.downloadUrl
                                ?.addOnSuccessListener { downloadUri ->
                                    // After uploading the image, create a new Food document
                                    // with the same document ID as the image
                                    val food = Food(
                                        id = task.metadata?.name, // Use the image's document ID
                                        foodName = editFoodName,
                                        foodDes = editDes,
                                        userId = userId,
                                        image = downloadUri.toString(),
                                        quantity = quantity
                                    )

                                    // Store the Food object in Firestore with the same document ID
                                    db.collection("foodPendingDonor").document(task.metadata?.name ?: "")
                                        .set(food)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "Upload Successful",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { error ->
                                            Toast.makeText(
                                                this,
                                                error.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(this, "Upload failed: $error", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Handle the case where uri is not initialized (e.g., show an error message)
                    Toast.makeText(this, "Please upload an image of food", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                // Handle the case where the user is not signed in
                Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
            }
        }


        binding.cancelBtn.setOnClickListener {
            finish()
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { resultUri ->
                uri = resultUri
                image?.setImageURI(resultUri)
            })

        binding.browseBtn.setOnClickListener {
            // Request storage permission when the user clicks the "Browse" button
            requestStoragePermission()
            // Launch the image picker after requesting permission
            galleryImage.launch("image/*")
        }

        // binding.uploadBtn.setOnClickListener {
//            if (uri != null) {
//                val userId = FirebaseAuth.getInstance().currentUser?.uid
//                if (userId != null) {
//                    val storageRef = storageRef.getReference("images").child(System.currentTimeMillis().toString())
//                    storageRef.putFile(uri!!)
//                        .addOnSuccessListener { task ->
//                            task.metadata?.reference?.downloadUrl
//                                ?.addOnSuccessListener { downloadUri ->
//                                    // After uploading the image, create a new Food document
//                                    // with the same document ID as the image
//                                    val food = Food(
//                                        id = task.metadata?.name, // Use the image's document ID
//                                        foodName = editFoodName.text.toString(),
//                                        foodDes = editDes.text.toString(),
//                                        userId = userId,
//                                        image = downloadUri.toString()
//                                    )
//
//                                    // Store the Food object in Firestore with the same document ID
//                                    db.collection("foodPendingDonar").document(task.metadata?.name ?: "")
//                                        .set(food)
//                                        .addOnSuccessListener {
//                                            Toast.makeText(
//                                                this,
//                                                "Upload Successful",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//                                        .addOnFailureListener { error ->
//                                            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
//                                        }
//                                }
//                        }
//                        .addOnFailureListener { error ->
//                            Toast.makeText(this, "Upload failed: $error", Toast.LENGTH_SHORT).show()
//                        }
//                }
//            } else {
//                // Handle the case where uri is not initialized (e.g., show an error message)
//                Toast.makeText(this, "Please upload an image of food", Toast.LENGTH_SHORT).show()
//            }
        // }


        image?.setImageResource(R.drawable.baseline_image_24)

        // Resize the ImageView
        val newWidthInPixels = 300 // Adjust this value as needed
        val newHeightInPixels = 300 // Adjust this value as needed
        val layoutParams = image?.layoutParams
        layoutParams?.width = newWidthInPixels
        layoutParams?.height = newHeightInPixels
        image?.layoutParams = layoutParams
    }

    private fun requestStoragePermission() {
        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission hasn't been granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission hasn't been granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now proceed with uploading the image
                // Call the method or code to upload the image here
            } else {
                // Permission denied, show a message to the user or handle it accordingly
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


