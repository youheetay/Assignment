package com.example.assignment.Adapter

import android.app.AlertDialog
import android.content.ContentValues
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
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.Food
import com.example.assignment.FoodR
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AdminDonorAdapter(
    private val foodList: ArrayList<Food>,
    private val galleryImage: ActivityResultLauncher<String>
) :
    RecyclerView.Adapter<AdminDonorAdapter.MyViewHolder>() {

    private var imageUri: Uri? = null
    //private var imageView : ImageView? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminDonorAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_admin,parent,false)
        return MyViewHolder(itemView)
    }

    private fun updateFoodDetailsWithImage(
        holder: MyViewHolder,
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

    fun updateImageUri(newUri: Uri?) {
        imageUri = newUri
        notifyDataSetChanged() // Notify the adapter that the data has changed

    }

    override fun onBindViewHolder(holder: AdminDonorAdapter.MyViewHolder, position: Int) {

        val food: Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes
        holder.foodQty.text = food.quantity.toString()
        Glide.with(holder.itemView.context)
            .load(food.image) // Use the image URL from the Food object
            .into(holder.foodImage)

        //imageView = holder.itemView.findViewById(R.id.imageView)

        holder.editBtn.setOnClickListener {
            val positionUpdate = holder.adapterPosition
            val updateFoodDonor = foodList[positionUpdate]

            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.admin_edit_donor_card, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Edit Food")

            val browseBtn = dialogView.findViewById<Button>(R.id.browseBtn)
            val imageView = dialogView.findViewById<ImageView>(R.id.imageView)


            val textView2 = dialogView.findViewById<TextView>(R.id.textView2)
            val nameEditText = dialogView.findViewById<EditText>(R.id.editFoodNameDonor)
            val descriptionEditText = dialogView.findViewById<EditText>(R.id.editFoodDesDonor)
            val quantityEditText = dialogView.findViewById<NumberPicker>(R.id.editFoodNumDonor)
            quantityEditText.maxValue = 60
            quantityEditText.minValue = 1
            quantityEditText.wrapSelectorWheel = true
            quantityEditText.setOnValueChangedListener { numberPicker, oldValue, newValue ->
                textView2.text = "Quantity : $newValue"
            }

            nameEditText.setText(updateFoodDonor.foodName)
            descriptionEditText.setText(updateFoodDonor.foodDes)
            quantityEditText.value = updateFoodDonor?.quantity?.toInt() ?: 1


            alertDialogBuilder.setView(dialogView)

            if (imageUri != null) {
                //imageView?.setImageURI(imageUri)
                Glide.with(holder.itemView.context)
                    .load(imageUri)
                    .into(imageView)
            } else {
                //imageView.setImageURI(imageUri)
                Glide.with(holder.itemView.context)
                    .load(updateFoodDonor.image) // Use the image URL from the Food object
                    .into(imageView)
            }

            browseBtn.setOnClickListener {
                // Launch the image picker
                galleryImage.launch("image/*")
                updateImageUri(imageUri)

            }


            alertDialogBuilder.setPositiveButton("Update") { _, _ ->
                val newName = nameEditText.text.toString()
                val newDes = descriptionEditText.text.toString()
                val newQuantity = quantityEditText.value
                val db = FirebaseFirestore.getInstance()

                updateFoodDetailsWithImage(holder, position, newName, newDes, newQuantity, imageUri)

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
            val deleteFoodDonor = foodList[positionDelete]


            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Delete Food")
            alertDialogBuilder.setMessage("Confirm To Delete?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                foodList.remove(deleteFoodDonor)
                notifyItemRemoved(positionDelete)

                // Delete the notification from Firestore
                val db = FirebaseFirestore.getInstance()
                val food = db.collection("food")
                //val imageUrl = foodList[positionDelete].image.toString()
                // Use the correct document ID to delete the specific notification in Firestore
                val deleteFoodId = deleteFoodDonor.id// Assuming id is the correct document ID
                if (deleteFoodId != null) {
                    food.document(deleteFoodId)
                        .delete()
                        .addOnSuccessListener {
                            val storageRef = Firebase.storage.getReference("images/$deleteFoodId")
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
                                        "Error deleting Food from Firestore: $exception"
                                    )
                                    Toast.makeText(
                                        holder.itemView.context,
                                        "Not Delete Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                ContentValues.TAG,
                                "Error deleting Food from Firestore: $exception"
                            )
                            Toast.makeText(
                                holder.itemView.context,
                                "Not Delete Successful",
                                Toast.LENGTH_SHORT
                            ).show()
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
        val foodName : TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDes : TextView = itemView.findViewById(R.id.tvFoodDes)
        val foodQty : TextView = itemView.findViewById(R.id.tvFoodQty)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        val editBtn : Button = itemView.findViewById(R.id.editButton)
        val deleteBtn : Button = itemView.findViewById(R.id.deleteButton)
    }

}
