package com.example.assignment.Adapter

import android.app.AlertDialog
import android.content.ContentValues
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
import com.example.assignment.Food
import com.example.assignment.FoodReq
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminDonorAdapter(private val foodList: ArrayList<Food>) :
    RecyclerView.Adapter<AdminDonorAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminDonorAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminDonorAdapter.MyViewHolder, position: Int) {
        val food : Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes
        holder.foodQty.text = food.quantity.toString()

        holder.editBtn.setOnClickListener{
            val positionUpdate = holder.adapterPosition
            val updateFoodDonor = foodList[positionUpdate]

            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.admin_edit_donor_card, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Edit Food")

            val textView2 = dialogView.findViewById<TextView>(R.id.textView2)
            val nameEditText = dialogView.findViewById<EditText>(R.id.editFoodNameDonor)
            val descriptionEditText = dialogView.findViewById<EditText>(R.id.editFoodDesDonor)
            val quantityEditText = dialogView.findViewById<NumberPicker>(R.id.editFoodNumDonor)
            quantityEditText.maxValue = 60
            quantityEditText.minValue = 1
            quantityEditText.wrapSelectorWheel = true
            quantityEditText.setOnValueChangedListener { numberPicker, oldValue, newValue -> textView2.text = "Quantity : $newValue"
            }

            nameEditText.setText(updateFoodDonor.foodName)
            descriptionEditText.setText(updateFoodDonor.foodDes)
            quantityEditText.value = updateFoodDonor?.quantity?.toInt() ?: 1

            alertDialogBuilder.setView(dialogView)

            alertDialogBuilder.setPositiveButton("Update"){ _, _ ->
                val newName = nameEditText.text.toString()
                val newDes = descriptionEditText.text.toString()
                val newQuantity = quantityEditText.value
                val db = FirebaseFirestore.getInstance()

                val updatedData = mapOf(
                    "foodName" to newName,
                    "foodDes" to newDes,
                    "quantity" to newQuantity
                )

                db.collection("food").document(updateFoodDonor.id.toString()).update(updatedData)
                    .addOnSuccessListener {
                        updateFoodDonor.foodName = newName
                        updateFoodDonor.foodDes = newDes
                        updateFoodDonor.quantity = newQuantity
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

                // Use the correct document ID to delete the specific notification in Firestore
                val deleteFoodId = deleteFoodDonor.id// Assuming id is the correct document ID
                if (deleteFoodId != null) {
                    food.document(deleteFoodId)
                        .delete()
                        .addOnSuccessListener {
                            // change to snakbar
                            Toast.makeText(holder.itemView.context, "You have deleted successfully", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener { exception ->
                            Log.e(ContentValues.TAG, "Error deleting Food from Firestore: $exception")
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
        val foodName : TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDes : TextView = itemView.findViewById(R.id.tvFoodDes)
        val foodQty : TextView = itemView.findViewById(R.id.tvFoodQty)
        val editBtn : Button = itemView.findViewById(R.id.editButton)
        val deleteBtn : Button = itemView.findViewById(R.id.deleteButton)
    }
}
