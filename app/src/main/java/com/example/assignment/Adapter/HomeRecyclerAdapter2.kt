package com.example.assignment.Adapter

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.Food
import com.example.assignment.R
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class HomeRecyclerAdapter2 (private val context: Context, private val foodList: ArrayList<Food>, private val parentContext: Context ): RecyclerView.Adapter<HomeRecyclerAdapter2.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item2, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val food: Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes
        holder.quantity.text = food.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(food.image) // Use the image URL from the Food object
            .into(holder.foodImage)

        holder.donateBtn.setOnClickListener {
            showConfirmationDialog(holder.adapterPosition) // Pass the item position to the dialog
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDes: TextView = itemView.findViewById(R.id.tvFoodDes)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        val donateBtn: ImageView = itemView.findViewById(R.id.donateBtn)
        val quantity: TextView = itemView.findViewById(R.id.selectQuantity)

    }

    public fun showConfirmationDialog(itemPosition: Int) {
        val builder = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.activity_quantity_dialog, null)

        val quantityPicker = dialogView.findViewById<NumberPicker>(R.id.selectQuantity)
        quantityPicker.maxValue = 60
        quantityPicker.minValue = 1

        builder.setView(dialogView)
            .setTitle("Select Quantity To Donate")
            .setPositiveButton("Donate") { dialogInterface: DialogInterface, _: Int ->
                val selectedQuantity = quantityPicker.value
                val food = foodList[itemPosition]
                val oldQuantity = food.quantity ?: 0
                val newQuantity = oldQuantity - selectedQuantity

                // Inside your showConfirmationDialog function, after making local changes:
                if (newQuantity >= 0) {
                    // Update the quantity in the data source (foodList)
                    foodList[itemPosition].quantity = newQuantity

                    // Update the Firebase Firestore document with the new quantity
                    val db = FirebaseFirestore.getInstance()

                    // Get the document ID from the food object
                    val foodDocumentId = food.id.toString()

                    // Create a map with the updated quantity
                    val updatedData = hashMapOf(
                        "quantity" to newQuantity
                    )

                    // Update the document with the specified ID using set() with merge option
                    db.collection("food").document(foodDocumentId)
                        .set(updatedData, SetOptions.merge())
                        .addOnSuccessListener {
                            showSuccessDialog()
                        }
                        .addOnFailureListener { e ->
                            showErrorDialog(e.message)
                        }
                } else {
                    // Handle the case where the new quantity is negative (optional)
                }
                // Check if newQuantity is 0 and delete the item
                if (newQuantity == 0) {
                    val positionDelete = itemPosition
                    val deleteFood = foodList[positionDelete]
                    // Remove the notification from the list locally
                    foodList.remove(deleteFood)
                    notifyItemRemoved(positionDelete)

                    // Delete the notification from Firestore
                    val db = FirebaseFirestore.getInstance()
                    val food = db.collection("food")

                    // Use the correct document ID to delete the specific notification in Firestore
                    val deleteFoodId = deleteFood.id // Assuming id is the correct document ID
                    if (deleteFoodId != null) {
                        food.document(deleteFoodId)
                            .delete()
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener { exception ->
                                Log.e(ContentValues.TAG, "Error deleting Food from Firestore: $exception")
                            }
                    }

                }

                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showSuccessDialog() {
        val successDialog = AlertDialog.Builder(parentContext)
            .setTitle("Success")
            .setMessage("Donate successfully.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Navigate back to your notification list fragment
                // You can use fragmentManager.popBackStack() or other navigation methods
            }
            .create()

        successDialog.show()
    }
    private fun showErrorDialog(errorMessage: String?) {
        val errorDialog = AlertDialog.Builder(parentContext)
            .setTitle("Error")
            .setMessage("Failed to Donate: $errorMessage")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        errorDialog.show()
    }
}