package com.example.assignment.Adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
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
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.FoodR
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Integer.parseInt

class AdminReqAdapter (private val foodList: ArrayList<FoodR>,
                       private val galleryImage: ActivityResultLauncher<String>
) :
    RecyclerView.Adapter<AdminReqAdapter.MyViewHolder>() {
    private var imageUri: Uri? = null
    private var imageView : ImageView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminReqAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_admin,parent,false)
        return MyViewHolder(itemView)
    }

    private fun updateFoodDetailsWithImage(
        holder: AdminReqAdapter.MyViewHolder,
        position: Int,
        newName: String,
        newDes: String,
        newQuantity: Int,
        uri: Uri? // Pass the selected image URI as a parameter
    ) {

        if (uri == null) {
            val db = FirebaseFirestore.getInstance()
            val updatedData = mapOf(
                "foodRName" to newName,
                "foodRDes" to newDes,
                "quantity" to newQuantity
            )

            db.collection("foodR").document(foodList[position].id.toString())
                .update(updatedData)
                .addOnSuccessListener {
                    foodList[position].foodNameR = newName
                    foodList[position].foodDesR = newDes
                    foodList[position].quantity = newQuantity
                    notifyItemChanged(position)
                    Toast.makeText(
                        holder.itemView.context,
                        "Update Success",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        holder.itemView.context,
                        "Error updating food: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            // Handle the case when a new image is selected
            val db = FirebaseFirestore.getInstance()

            // Upload the selected image to Firebase Storage
            val storageRef =
                Firebase.storage.getReference("images").child(System.currentTimeMillis().toString())
            storageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                        // Update the food details with the new image URL
                        val updatedData = mapOf(
                            "foodNameR" to newName,
                            "foodDesR" to newDes,
                            "quantity" to newQuantity,
                            "image" to downloadUri.toString() // Update the image URL
                        )

                        db.collection("foodR").document(foodList[position].id.toString())
                            .update(updatedData)
                            .addOnSuccessListener {
                                foodList[position].foodNameR = newName
                                foodList[position].foodDesR = newDes
                                foodList[position].quantity = newQuantity
                                foodList[position].image =
                                    downloadUri.toString() // Update the image URL
                                notifyItemChanged(position)
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Update Success",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Error updating food: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        holder.itemView.context,
                        "Error uploading image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun updateImageUri(newUri: Uri?) {
        imageUri = newUri
        notifyDataSetChanged() // Notify the adapter that the data has changed
        imageView?.setImageURI(imageUri)
    }

    override fun onBindViewHolder(holder: AdminReqAdapter.MyViewHolder, position: Int) {
        val food : FoodR = foodList[position]
        holder.foodNameR.text = food.foodNameR
        holder.foodDesR.text = food.foodDesR
        holder.foodQty.text = food.quantity.toString()
        Glide.with(holder.itemView.context)
            .load(food.image) // Use the image URL from the Food object
            .into(holder.foodImage)


        holder.editBtn.setOnClickListener{
            val positionUpdate = holder.adapterPosition
            val updateFoodReq = foodList[positionUpdate]

            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.admin_edit_req_card, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Edit Food")

            val browseBtn = dialogView.findViewById<Button>(R.id.browseBtn)
            //val imageView = dialogView.findViewById<ImageView>(R.id.imageView)
            imageView = dialogView.findViewById(R.id.imageView)

            val textView2 = dialogView.findViewById<TextView>(R.id.textView2)
            val nameEditText = dialogView.findViewById<EditText>(R.id.editFoodNameReq)
            val descriptionEditText = dialogView.findViewById<EditText>(R.id.editFoodDesReq)
            val quantityEditText = dialogView.findViewById<NumberPicker>(R.id.editFoodNumReq)
            quantityEditText.maxValue = 60
            quantityEditText.minValue = 1
            quantityEditText.wrapSelectorWheel = true
            quantityEditText.setOnValueChangedListener { numberPicker, oldValue, newValue -> textView2.text = "Quantity : $newValue"
            }

            nameEditText.setText(updateFoodReq.foodNameR)
            descriptionEditText.setText(updateFoodReq.foodDesR)
            quantityEditText.value = updateFoodReq?.quantity?.toInt() ?: 1

            alertDialogBuilder.setView(dialogView)

            imageView?.let { iv ->
                // Load the image using Glide if imageUri is not null
                if (imageUri != null) {
                    Glide.with(holder.itemView.context)
                        .load(imageUri)
                        .into(iv)
                } else {
                    iv.setImageURI(null) // Clear the ImageView if no image is selected
                    Glide.with(holder.itemView.context)
                        .load(updateFoodReq.image) // Use the image URL from the Food object
                        .into(iv)
                }
            }

            browseBtn.setOnClickListener {
                // Launch the image picker
                galleryImage.launch("image/*")
                updateImageUri(imageUri)

            }

            alertDialogBuilder.setPositiveButton("Update"){ _, _ ->
                val newName = nameEditText.text.toString()
                val newDes = descriptionEditText.text.toString()
                val newQuantity = quantityEditText.value
                val db = FirebaseFirestore.getInstance()

                updateFoodDetailsWithImage(holder, position, newName, newDes, newQuantity, imageUri)

                imageUri = null
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                imageUri = null
                dialog.dismiss()
            }

            val newWidthInPixels = 300 // Adjust this value as needed
            val newHeightInPixels = 300 // Adjust this value as needed
            val layoutParams = imageView?.layoutParams
            layoutParams?.width = newWidthInPixels
            layoutParams?.height = newHeightInPixels
            imageView?.layoutParams = layoutParams
            alertDialogBuilder.show()

        }

        holder.deleteBtn.setOnClickListener{
            val positionDelete = holder.adapterPosition
            val deleteFoodReq = foodList[positionDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Delete Food")
            alertDialogBuilder.setMessage("Confirm To Delete?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                foodList.remove(deleteFoodReq)
                notifyItemRemoved(positionDelete)

                // Delete the notification from Firestore
                val db = FirebaseFirestore.getInstance()
                val food = db.collection("foodR")

                // Use the correct document ID to delete the specific notification in Firestore
                val deleteFoodId = deleteFoodReq.id// Assuming id is the correct document ID
                if (deleteFoodId != null) {
                    food.document(deleteFoodId)
                        .delete()
                        .addOnSuccessListener {
                            val storageRef = Firebase.storage.getReference("images/$deleteFoodId")
                            if(storageRef != null){
                                storageRef.delete()
                                    .addOnSuccessListener {
                                        // change to snakbar
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "You have deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e(
                                            ContentValues.TAG,
                                            "Error deleting image from Firestore: $exception"
                                        )
                                        Toast.makeText(
                                            holder.itemView.context,
                                            "Not Delete Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }

                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Error deleting Food from Firestore: $exception")
                            Toast.makeText(holder.itemView.context, "Not Delete Successful", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialogBuilder.show()
        }

    }


    override fun getItemCount(): Int {
        return foodList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodNameR : TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDesR : TextView = itemView.findViewById(R.id.tvFoodDes)
        val foodQty : TextView = itemView.findViewById(R.id.tvFoodQty)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        val editBtn : Button = itemView.findViewById(R.id.editButton)
        val deleteBtn : Button = itemView.findViewById(R.id.deleteButton)
    }

//    private fun showSuccessDialog() {
//        val successDialog = AlertDialog.Builder(parentContext)
//            .setTitle("Success")
//            .setMessage("Donate successfully.")
//            .setPositiveButton("OK") { dialog, _ ->
//                dialog.dismiss()
//                // Navigate back to your notification list fragment
//                // You can use fragmentManager.popBackStack() or other navigation methods
//            }
//            .create()
//
//        successDialog.show()
//    }
//    private fun showErrorDialog(errorMessage: String?) {
//        val errorDialog = AlertDialog.Builder(parentContext)
//            .setTitle("Error")
//            .setMessage("Failed to Donate: $errorMessage")
//            .setPositiveButton("OK") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//
//        errorDialog.show()
//    }


}

