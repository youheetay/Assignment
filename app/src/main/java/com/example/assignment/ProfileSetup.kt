//package com.example.assignment
//
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Toast
//import com.example.assignment.databinding.ActivityProfileSetupBinding
//import com.google.firebase.auth.ActionCodeUrl
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//
//class ProfileSetup : AppCompatActivity() {
//
//    private lateinit var  binding : ActivityProfileSetupBinding
//    private lateinit var  auth : FirebaseAuth
//    private lateinit var databaseReference : DatabaseReference
//    private lateinit var storageReference: DatabaseReference
//    private lateinit var imageUri: ActionCodeUrl
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        auth = FirebaseAuth.getInstance()
//        val uid = auth.currentUser?.uid
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
//
//        binding.saveProfileBtn.setOnClickListener{
//
//            val userName = binding.profileName.text.toString()
//            val DOB = binding.dateOfBirth.text.toString()
//            val gender = binding.profileGender.text.toString()
//            val address = binding.profileAddress.text.toString()
//
//            val user = User(userName,gender,DOB,address)
//
//            if(uid != null){
//                databaseReference.child(uid).setValue(user).addOnCompleteListener{
//                    if (it.isSuccessful){
//
//                        uploadProfilePic()
//
//                    }else{
//                        Toast.makeText(this@ProfileSetup, "Failed to update Profile",Toast.LENGTH_SHORT).show()
//                    }
//                }
//             }
//        }
//
//    }
//
////    private fun uploadProfilePic() {
////
////        val imageUri = Uri.parse("android.resource://$packageName/${R.drawable.img}")
////        // Upload the image to Firebase Storage
////        storageReference.child("Users/${auth.currentUser?.uid}").putFile(imageUri)
////            .addOnSuccessListener { taskSnapshot ->
////                // Image upload was successful
////                // You can get the download URL of the uploaded image like this:
////                val downloadUrl = taskSnapshot.storage.downloadUrl.toString()
////
////                // Now, you can use the 'downloadUrl' to store the image URL in Firestore or wherever you need.
////            }
////            .addOnFailureListener { exception ->
////                // Handle any errors that occurred during the upload
////                // You might want to display an error message to the user or log the error for debugging.
////            }
////
////    }
//}