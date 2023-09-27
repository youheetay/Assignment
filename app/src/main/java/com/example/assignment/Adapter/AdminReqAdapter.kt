package com.example.assignment.Adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
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
import com.example.assignment.FoodReq
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Integer.parseInt

class AdminReqAdapter (private val foodList: ArrayList<FoodReq>) :
    RecyclerView.Adapter<AdminReqAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminReqAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminReqAdapter.MyViewHolder, position: Int) {
        val food : FoodReq = foodList[position]
        holder.foodNameR.text = food.foodNameR
        holder.foodDesR.text = food.foodDesR
        holder.foodQty.text = food.quantity.toString()


        holder.editBtn.setOnClickListener{
            val positionUpdate = holder.adapterPosition
            val updateFoodReq = foodList[positionUpdate]

            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.admin_edit_req_card, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Edit Food")

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

            alertDialogBuilder.setPositiveButton("Update"){ _, _ ->
                val newName = nameEditText.text.toString()
                val newDes = descriptionEditText.text.toString()
                val newQuantity = quantityEditText.value
                val db = FirebaseFirestore.getInstance()


                //updateFood(positionUpdate, newName, newDes, newQuantity)

                val updatedData = mapOf(
                    "foodNameR" to newName,
                    "foodDesR" to newDes,
                    "quantity" to newQuantity
                )

                db.collection("foodR").document(updateFoodReq.id.toString()).update(updatedData)
                    .addOnSuccessListener {
                        updateFoodReq.foodNameR = newName
                        updateFoodReq.foodDesR = newDes
                        updateFoodReq.quantity = newQuantity
                        notifyItemChanged(position)
                        Toast.makeText(holder.itemView.context, "Update Success", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(holder.itemView.context, "Error updating food: ${e.message}", Toast.LENGTH_SHORT).show()
                    }


            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
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
                            // change to snakbar
                            Toast.makeText(holder.itemView.context, "You have deleted successfully", Toast.LENGTH_SHORT).show()

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
        val editBtn : Button = itemView.findViewById(R.id.editButton)
        val deleteBtn : Button = itemView.findViewById(R.id.deleteButton)
    }
}

