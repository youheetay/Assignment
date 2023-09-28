package com.example.assignment

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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ReqHistoryAdapter (private val reqFoodList: ArrayList<FoodR>,
                         private val galleryImage: ActivityResultLauncher<String>
):
    RecyclerView.Adapter<ReqHistoryAdapter.ViewHolderHistory>() {


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
                "foodNameR" to newName,
                "foodDesR" to newDes,
                "quantity" to newQuantity
            )

            db.collection("foodR").document(reqFoodList[position].id.toString())
                .update(updatedData)
                .addOnSuccessListener {
                    reqFoodList[position].foodNameR = newName
                    reqFoodList[position].foodDesR = newDes
                    reqFoodList[position].quantity = newQuantity
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

                        db.collection("foodR").document(reqFoodList[position].id.toString())
                            .update(updatedData)
                            .addOnSuccessListener {
                                reqFoodList[position].foodNameR = newName
                                reqFoodList[position].foodDesR = newDes
                                reqFoodList[position].quantity = newQuantity
                                reqFoodList[position].image =
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

    private var imageUri: Uri? = null

    companion object {

        const val ARG_FOOD = "foodR"
        const val STORAGE_PERMISSION_CODE = 1 // You can use any unique integer value here
    }

    // Declare variables to store the selected image Uri and ImageView
    private var uri: Uri? = null
    private var image: ImageView? = null
    fun updateImageUri(newUri: Uri?) {
        imageUri = newUri
        image?.setImageURI(newUri)
        notifyDataSetChanged() // Notify the adapter that the data has changed

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReqHistoryAdapter.ViewHolderHistory {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reqlist_item2,parent,false)
        return ViewHolderHistory(itemView)
    }



    override fun onBindViewHolder(holder: ReqHistoryAdapter.ViewHolderHistory, position: Int) {
        val food : FoodR = reqFoodList[position]
        holder.foodNameR.text  = food.foodNameR
        holder.foodDesR.text = food.foodDesR
        holder.quantity.text = food.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(food.image) // Use the image URL from the Food object
            .into(holder.image)

        holder.editBtn.setOnClickListener {
            val positionUpdate = holder.adapterPosition
            val updateFoodRequestor = reqFoodList[positionUpdate]

            val dialogView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.edit_req_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Edit Food")

            val browseBtn = dialogView.findViewById<Button>(R.id.browseBtn)
            val imageView = dialogView.findViewById<ImageView>(R.id.imageView)


            val textView2 = dialogView.findViewById<TextView>(R.id.quantityRequestor)
            val nameEditText = dialogView.findViewById<EditText>(R.id.editFoodName)
            val descriptionEditText = dialogView.findViewById<EditText>(R.id.editDes)
            val quantityEditText = dialogView.findViewById<NumberPicker>(R.id.editQuantity)
            quantityEditText.maxValue = 60
            quantityEditText.minValue = 1
            quantityEditText.wrapSelectorWheel = true
            quantityEditText.setOnValueChangedListener { numberPicker, oldValue, newValue ->
                textView2.text = "Quantity : $newValue"
            }

            nameEditText.setText(updateFoodRequestor.foodNameR)
            descriptionEditText.setText(updateFoodRequestor.foodDesR)
            quantityEditText.value = updateFoodRequestor?.quantity?.toInt() ?: 1


            alertDialogBuilder.setView(dialogView)

            if (imageUri != null) {
                //imageView?.setImageURI(imageUri)
                Glide.with(holder.itemView.context)
                    .load(imageUri)
                    .into(imageView)
            } else {
                //imageView.setImageURI(imageUri)
                Glide.with(holder.itemView.context)
                    .load(updateFoodRequestor.image) // Use the image URL from the Food object
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


        holder.deleteBtn.setOnClickListener{
            val positionDelete = holder.adapterPosition
            val deleteFood = reqFoodList[positionDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Delete Food")
            alertDialogBuilder.setMessage("Confirm To Delete?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                reqFoodList.remove(deleteFood)
                notifyItemRemoved(positionDelete)

                // Delete the notification from Firestore
                val db = FirebaseFirestore.getInstance()
                val food = db.collection("foodR")

                // Use the correct document ID to delete the specific notification in Firestore
                val deleteFoodId = deleteFood.id// Assuming id is the correct document ID
                if (deleteFoodId != null) {
                    food.document(deleteFoodId)
                        .delete()
                        .addOnSuccessListener {
                            // change to snakbar
                            Toast.makeText(holder.itemView.context, "You have deleted successfully", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener { exception ->
                            Log.e(ContentValues.TAG, "Error deleting Food from Firestore: $exception")
                            Toast.makeText(holder.itemView.context, "Not Delete Succesful", Toast.LENGTH_SHORT).show()
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
        return reqFoodList.size
    }

    public class ViewHolderHistory(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodNameR : TextView = itemView.findViewById(R.id.foodNameReq)
        val foodDesR : TextView = itemView.findViewById(R.id.foodDesR)
        val quantity : TextView = itemView.findViewById(R.id.quantityReq)
        val image: ImageView = itemView.findViewById(R.id.imageView2)
        val editBtn: Button = itemView.findViewById(R.id.editHBtn)
        val deleteBtn: Button = itemView.findViewById(R.id.dltHBtn)
        val documentId: String? = null

    }
}