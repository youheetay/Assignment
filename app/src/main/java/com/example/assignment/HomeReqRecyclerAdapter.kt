package com.example.assignment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class HomeReqRecyclerAdapter(private val context: Context, private val foodReqList: ArrayList<FoodR>,private val parentContext: Context):
    RecyclerView.Adapter<HomeReqRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): HomeReqRecyclerAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reqlist_item,parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeReqRecyclerAdapter.MyViewHolder, position: Int) {
        val foodR: FoodR = foodReqList[position]
        holder.foodNameReq.text = foodR.foodNameR
        holder.foodDesReq.text = foodR.foodDesR
        holder.quantity.text = foodR.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(foodR.image) // Use the image URL from the FoodR object
            .into(holder.foodImage)

        holder.donateBtn.setOnClickListener {
            showConfirmationDialog(holder.adapterPosition) // Pass the item position to the dialog
        }
    }

    override fun getItemCount(): Int {
        return foodReqList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodNameReq : TextView = itemView.findViewById(R.id.tvFoodNameR)
        val foodDesReq : TextView = itemView.findViewById(R.id.tvFoodDesR)
        val quantity : TextView = itemView.findViewById(R.id.selectQuantity)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        val donateBtn: ImageView = itemView.findViewById(R.id.donateBtn)
    }

    public fun showConfirmationDialog(itemPosition: Int) {
        val builder = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.donate_dialog, null) // Create a custom dialog layout

        val quantityPicker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)
        quantityPicker.maxValue = 60
        quantityPicker.minValue = 1

        builder.setView(dialogView)
            .setTitle("Select Quantity To Donate")
            .setPositiveButton("Donate") { dialogInterface: DialogInterface, _: Int ->
                val selectedQuantity = quantityPicker.value
                val food = foodReqList[itemPosition]
                val oldQuantity = food.quantity ?: 0
                val newQuantity = oldQuantity - selectedQuantity

                // Inside your showConfirmationDialog function, after making local changes:
                if (newQuantity <= oldQuantity) {
                    // Update the quantity in the data source (foodList)
                    foodReqList[itemPosition].quantity = newQuantity

                    // Update the Firebase Firestore document with the new quantity
                    val db = FirebaseFirestore.getInstance()

                    // Get the document ID from the food object
                    val foodDocumentId = food.id.toString()

                    // Create a map with the updated quantity
                    val updatedData = hashMapOf(
                        "quantity" to newQuantity
                    )

                    // Update the document with the specified ID using set() with merge option
                    db.collection("foodR").document(foodDocumentId)
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