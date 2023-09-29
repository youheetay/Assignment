package com.example.assignment.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.Food
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HistoryAdapter(
    private val context: Context, private val foodList: ArrayList<Food>,
    private val galleryImage: ActivityResultLauncher<String>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<HistoryAdapter.ViewHolderHistory>() {

    companion object {
        const val ARG_FOOD = "food"
        const val STORAGE_PERMISSION_CODE = 1 // You can use any unique integer value here
    }

    // Declare variables to store the selected image Uri and ImageView
    private var uri: Uri? = null
    private var imageView: ImageView? = null


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderHistory {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolderHistory(itemView)
    }

    fun updateImageUri(newUri: Uri?) {
        uri = newUri
        imageView?.setImageURI(newUri)
        notifyDataSetChanged() // Notify the adapter that the data has changed

    }

    private fun updateFoodDetailsWithImage(
        holder: ViewHolderHistory,
        position: Int,
        newName: String,
        newDes: String,
        newQuantity: Int,
        uri: Uri? // Pass the selected image URI as a parameter
    ) {

        if (uri == null) {
            val db = FirebaseFirestore.getInstance()
            val updatedData = mapOf(
                "foodName" to newName,
                "foodDes" to newDes,
                "quantity" to newQuantity
            )

            db.collection("food").document(foodList[position].id.toString())
                .update(updatedData)
                .addOnSuccessListener {
                    foodList[position].foodName = newName
                    foodList[position].foodDes = newDes
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
                            "foodName" to newName,
                            "foodDes" to newDes,
                            "quantity" to newQuantity,
                            "image" to downloadUri.toString() // Update the image URL
                        )

                        db.collection("food").document(foodList[position].id.toString())
                            .update(updatedData)
                            .addOnSuccessListener {
                                foodList[position].foodName = newName
                                foodList[position].foodDes = newDes
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

    override fun onBindViewHolder(holder: ViewHolderHistory, position: Int) {
        val food: Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes
        holder.quantity.text = food.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(food.image) // Use the image URL from the Food object
            .into(holder.foodImage)

        holder.editBtn.setOnClickListener {
            val position = holder.adapterPosition
            val foodEdit = foodList[position]

            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.activity_edit_page_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)


            val browseBtn = dialogView.findViewById<Button>(R.id.browseBtn)
            imageView = dialogView.findViewById<ImageView>(R.id.imageView)

            val editFoodName = dialogView.findViewById<EditText>(R.id.editFoodName)
            val editFoodDes = dialogView.findViewById<EditText>(R.id.editDes)
            val showQuantity = dialogView.findViewById<TextView>(R.id.quantityDonar)

            val editQuantity = dialogView.findViewById<NumberPicker>(R.id.editQuantity)
            editQuantity.maxValue = 60
            editQuantity.minValue = 1
            editQuantity.wrapSelectorWheel = true
            editQuantity.setOnValueChangedListener { _, _, newValue ->
                showQuantity.text = "Quantity : $newValue"
            }

            editFoodName.setText(foodEdit.foodName)
            editFoodDes.setText(foodEdit.foodDes)
            editQuantity.value = food.quantity.toString().toInt()

            alertDialogBuilder.setView(dialogView)



            imageView?.let { iv ->
                // Load the image using Glide if imageUri is not null
                if (uri != null) {
                    Glide.with(holder.itemView.context)
                        .load(uri)
                        .into(iv)
                } else {
                    iv.setImageURI(null) // Clear the ImageView if no image is selected
                    Glide.with(holder.itemView.context)
                        .load(foodEdit.image) // Use the image URL from the Food object
                        .into(iv)
                }
            }

            browseBtn.setOnClickListener {
                // Launch the image picker
                galleryImage.launch("image/*")
                updateImageUri(uri)
                imageView?.setImageURI(uri)

            }

            alertDialogBuilder.setPositiveButton("Update") { _, _ ->
                val newName = editFoodName.text.toString()
                val newDes = editFoodDes.text.toString()
                val newQuantity = editQuantity.value
                val db = FirebaseFirestore.getInstance()

                updateFoodDetailsWithImage(holder, position, newName, newDes, newQuantity, uri)

            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
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

        holder.deleteBtn.setOnClickListener {
            val positionDelete = holder.adapterPosition
            val deleteFood = foodList[positionDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Delete Food")
            alertDialogBuilder.setMessage("Confirm To Delete?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                foodList.remove(deleteFood)
                notifyItemRemoved(positionDelete)

                // Delete the food from Firestore
                val db = FirebaseFirestore.getInstance()
                val food = db.collection("food")

                // Use the correct document ID to delete the specific food in Firestore
                val deleteFoodId = deleteFood.id// Assuming id is the correct document ID
                if (deleteFoodId != null) {
                    food.document(deleteFoodId)
                        .delete()
                        .addOnSuccessListener {
                            val storageRef = Firebase.storage.getReference("images/$deleteFoodId")
                            if (storageRef != null) {
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

    public class ViewHolderHistory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDes: TextView = itemView.findViewById(R.id.tvFoodDes)
        val editBtn: Button = itemView.findViewById(R.id.editBtn)
        val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)
        val documentId: String? = null
        val quantity: TextView = itemView.findViewById(R.id.selectQuantity)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
    }

    private fun requestStoragePermission(activity: Activity) {
        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission hasn't been granted, request it
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
        if (ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission hasn't been granted, request it
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }


}