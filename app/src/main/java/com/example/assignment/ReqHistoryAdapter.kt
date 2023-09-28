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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ReqHistoryAdapter (private val reqFoodList: ArrayList<FoodR> ):
    RecyclerView.Adapter<ReqHistoryAdapter.ViewHolderHistory>() {

    companion object {
        const val ARG_FOOD = "foodR"
        const val STORAGE_PERMISSION_CODE = 1 // You can use any unique integer value here
    }

    // Declare variables to store the selected image Uri and ImageView
    private var uri: Uri? = null
    private var image: ImageView? = null

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
            val foodEdit = reqFoodList[position]
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.edit_req_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Update Food")

            val editFoodName = dialogView.findViewById<EditText>(R.id.editFoodName)
            val editFoodDes = dialogView.findViewById<EditText>(R.id.editDes)
            val showQuantity = dialogView.findViewById<TextView>(R.id.quantityRequestor)

            val editQuantity = dialogView.findViewById<NumberPicker>(R.id.editQuantity)
            editQuantity.maxValue = 60
            editQuantity.minValue = 1
            editQuantity.wrapSelectorWheel = true
            editQuantity.setOnValueChangedListener { _, _, newValue ->
                showQuantity.text = "Quantity : $newValue"
            }

            editFoodName.setText(foodEdit.foodNameR)
            editFoodDes.setText(foodEdit.foodDesR)
            editQuantity.value = food.quantity.toString().toInt()

            alertDialogBuilder.setView(dialogView)

            alertDialogBuilder.setPositiveButton("Update") { _, _ ->
                val newName = editFoodName.text.toString()
                val newDes = editFoodDes.text.toString()
                val newQuantity = editQuantity.value
                val db = FirebaseFirestore.getInstance()

                val foodId = foodEdit.id

                if (foodId != null) {
                    val updatedData = mapOf(
                        "foodNameR" to newName,
                        "foodDesR" to newDes,
                        "quantity" to newQuantity
                    )

                    db.collection("food").document(foodId).set(updatedData, SetOptions.merge())
                        .addOnSuccessListener {
                            foodEdit.foodNameR = newName
                            foodEdit.foodDesR = newDes
                            foodEdit.quantity = newQuantity
                            notifyItemChanged(position)
                            Toast.makeText(holder.itemView.context, "Update Success", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(holder.itemView.context, "Error updating food: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            // Initialize the image and URI variables here
            image = dialogView.findViewById(R.id.imageView)
            uri?.let { image?.setImageURI(it) }

//            // Set up image selection logic
//            val galleryImage = (context as AppCompatActivity).registerForActivityResult(
//                ActivityResultContracts.GetContent(),
//                ActivityResultCallback { resultUri ->
//                    uri = resultUri
//                    image?.setImageURI(resultUri)
//                })
//
//            dialogView.findViewById<Button>(R.id.browseBtn).setOnClickListener {
//                requestStoragePermission(context as Activity)
//                galleryImage.launch("image/*")
//            }



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