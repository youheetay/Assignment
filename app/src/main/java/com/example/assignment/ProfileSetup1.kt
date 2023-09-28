package com.example.assignment

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import com.example.assignment.databinding.ActivityProfileSetupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileSetup1 : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = Firebase.firestore
    private var storageRef = Firebase.storage
    private var uri: Uri? = null // Initialize with null

    private val STORAGE_PERMISSION_CODE = 101 // Request code for storage permission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val browseBtn : Button = findViewById(R.id.browseImgBtn)
        val image : ImageView = findViewById(R.id.imageView3)
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateLabel(calendar)
        }
        val spinnerGender : Spinner = findViewById<Spinner>(R.id.spinnerGender)

        ArrayAdapter.createFromResource(
            this,
            R.array.gender,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerGender.adapter = adapter

        }


        firebaseAuth = FirebaseAuth.getInstance()

        binding.dateOfBirth.setOnClickListener{
            DatePickerDialog(this,datePicker,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()

        }


        binding.saveProfileBtn.setOnClickListener {
            val name = binding.profileName.text.toString()
            val gender = spinnerGender.selectedItem.toString()
            val dob = binding.dateOfBirth.text.toString()
            val address = binding.profileAddress.text.toString()

            if (name.isNotEmpty() && gender.isNotEmpty() && dob.isNotEmpty() && address.isNotEmpty()) {
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
                                        val userMap = User(
                                            image = downloadUri.toString(),
                                            userName = name,
                                            gender = gender,
                                            DOB = dob,
                                            address = address,
                                            userId = userId
                                        )

                                        // Reference a new document with a generated ID
                                        db.collection("user").document(userId).set(userMap)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "Successfully save profile!",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                                val intent = Intent(this, UserActivity::class.java)

                                                startActivity(intent)
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    this,
                                                    "Failed To save profile!",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                                // Finish the current activity to go back to the previous page
                                                finish()
                                            }
                                    }
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(this, "Upload failed: $error", Toast.LENGTH_SHORT)
                                    .show()
                            }


                    }

                    val cancelBtn = findViewById<Button>(R.id.backProfileBtn)
                    cancelBtn.setOnClickListener {
                        val intent = Intent(this, UserActivity::class.java)

                        startActivity(intent)
                    }


                }
            }
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { resultUri ->
                uri = resultUri
                image?.setImageURI(resultUri)
            })

        browseBtn.setOnClickListener {
            // Request storage permission when the user clicks the "Browse" button
            requestStoragePermission()
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
    }

    private fun updateLabel(calendar: Calendar) {
             val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.UK)
        binding.dateOfBirth.setText(sdf.format(calendar.time))
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