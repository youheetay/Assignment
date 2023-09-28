package com.example.assignment

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.assignment.databinding.ActivityLoginBinding
import com.example.assignment.databinding.ActivityProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var uid : String
    private lateinit var historyBtn: Button
    private var imageUri: Uri? = null // Initialize this variable as needed
    // Initialize your ActivityResultLauncher
    private val galleryImage: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        historyBtn = findViewById(R.id.historyBtn)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.buttonNavigationView)

        if (uid.isNotEmpty()) {
            getUserData()
        }

        binding.editProfileBtn.setOnClickListener{
            val Intent = Intent(this, HistoryViewActivity::class.java)
            startActivity(Intent)
        }


        binding.logoutBtn.setOnClickListener{
            auth.signOut()
            startActivity( Intent(this,LoginActivity::class.java))
        }

        binding.historyBtn.setOnClickListener{
            val historyIntent = Intent(this, HistoryViewActivity::class.java)
            startActivity(historyIntent)
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
    }

    private fun getUserData() {
        db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("user").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {

                        val user = document.toObject(User::class.java)
                        Glide.with(this) // Use 'this' for the activity context
                            .load(user?.image) // Use the image URL from the User object
                            .override(300, 300) // Set a fixed size of 300x300 pixels
                            .into(binding.imageView)

                        if (user != null) {
                            binding.userId.text = user.userId
                            binding.userName.text = user.userName
                            binding.gender.text = user.gender
                            binding.dob.text = user.DOB
                            binding.address.text = user.address

                        }

                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
//
//            // Function to show the edit dialog
//            private fun EditDialog(holder: HomeRecyclerAdapter.MyViewHolder) {
//                val positionUpdate = holder.adapterPosition
//                val updateFoodRequestor = User[positionUpdate]
//
//                val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_req_dialog, null)
//                val alertDialogBuilder = AlertDialog.Builder(this)
//                alertDialogBuilder.setTitle("Edit Food")
//
//                val browseBtn = dialogView.findViewById<Button>(R.id.browseBtn)
//                val imageView = dialogView.findViewById<ImageView>(R.id.imageView)
//
//                val textView2 = dialogView.findViewById<TextView>(R.id.quantityRequestor)
//                val nameEditText = dialogView.findViewById<EditText>(R.id.editFoodName)
//                val descriptionEditText = dialogView.findViewById<EditText>(R.id.editDes)
//                val quantityEditText = dialogView.findViewById<NumberPicker>(R.id.editQuantity)
//                quantityEditText.maxValue = 60
//                quantityEditText.minValue = 1
//                quantityEditText.wrapSelectorWheel = true
//                quantityEditText.setOnValueChangedListener { numberPicker, oldValue, newValue ->
//                    textView2.text = "Quantity : $newValue"
//                }
//
//                nameEditText.setText(updateFoodRequestor.foodNameR)
//                descriptionEditText.setText(updateFoodRequestor.foodDesR)
//                quantityEditText.value = updateFoodRequestor?.quantity?.toInt() ?: 1
//
//                alertDialogBuilder.setView(dialogView)
//
//                if (imageUri != null) {
//                    Glide.with(this)
//                        .load(imageUri)
//                        .into(imageView)
//                } else {
//                    Glide.with(this)
//                        .load(updateFoodRequestor.image) // Use the image URL from the Food object
//                        .into(imageView)
//                }
//
//                browseBtn.setOnClickListener {
//                    // Launch the image picker
//                    galleryImage.launch("image/*")
//                }
//
//                alertDialogBuilder.setPositiveButton("Update") { _, _ ->
//                    val newName = nameEditText.text.toString()
//                    val newDes = descriptionEditText.text.toString()
//                    val newQuantity = quantityEditText.value
//                    val db = FirebaseFirestore.getInstance()
//
//                    updateFoodDetailsWithImage(holder, position, newName, newDes, newQuantity, imageUri)
//                }
//                alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
//                    dialog.dismiss()
//                }
//
//                val newWidthInPixels = 300 // Adjust this value as needed
//                val newHeightInPixels = 300 // Adjust this value as needed
//                val layoutParams = imageView?.layoutParams
//                layoutParams?.width = newWidthInPixels
//                layoutParams?.height = newHeightInPixels
//                imageView?.layoutParams = layoutParams
//                alertDialogBuilder.show()
//            }
//
//
//            // Implement other methods and functionality as needed
//
//            private fun updateFoodDetailsWithImage(
//                holder: MyViewHolder,
//                position: Int,
//                newName: String,
//                newDes: String,
//                newQuantity: Int,
//                imageUri: Uri?
//            ) {
//                // Implement your update logic here
//            }
}