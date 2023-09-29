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
import com.bumptech.glide.Glide
import com.example.assignment.FoodR
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AdminReqPendingAdapter (private val foodList: ArrayList<FoodR>) :
    RecyclerView.Adapter<AdminReqPendingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminReqPendingAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pending_list,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminReqPendingAdapter.MyViewHolder, position: Int) {
        val food : FoodR = foodList[position]
        holder.foodNameR.text = food.foodNameR
        holder.foodDesR.text = food.foodDesR
        holder.foodQty.text = food.quantity.toString()
        Glide.with(holder.itemView.context)
            .load(food.image) // Use the image URL from the Food object
            .into(holder.foodImage)

        holder.approveBtn.setOnClickListener{
            val positionDelete = holder.adapterPosition
            val deleteFoodReq = foodList[positionDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setMessage("Confirm To Approve?")
            alertDialogBuilder.setPositiveButton("Approve") { _, _ ->
                val db = FirebaseFirestore.getInstance()
                val sourceCollection  = db.collection("foodPendingReq")
                val targetCollection  = db.collection("foodR")
                val transferFoodId = deleteFoodReq.id// Assuming id is the correct document ID

                sourceCollection.document(transferFoodId.toString()).get()
                    .addOnSuccessListener { sourceDocumentSnapshot ->
                        if(sourceDocumentSnapshot.exists()){
                            val data = sourceDocumentSnapshot.data?: emptyMap()

                            targetCollection.document(transferFoodId.toString()).set(data)
                                .addOnSuccessListener {
                                    // Document moved successfully
                                    // Now, delete the original document from the source collection
                                    sourceCollection.document(transferFoodId.toString()).delete()
                                        .addOnSuccessListener {
                                            //Remove the notification from the list locally
                                            foodList.remove(deleteFoodReq)
                                            notifyItemRemoved(positionDelete)
                                            Toast.makeText(holder.itemView.context, "Approved Success", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(holder.itemView.context, "Failed delete", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(holder.itemView.context, "Failed transfer", Toast.LENGTH_SHORT).show()
                                }

                        }
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
                val food = db.collection("foodPendingReq")

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
        val foodNameR : TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDesR : TextView = itemView.findViewById(R.id.tvFoodDes)
        val foodQty : TextView = itemView.findViewById(R.id.tvFoodQty)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
        val approveBtn : Button = itemView.findViewById(R.id.approveBtn)
        val deleteBtn : Button = itemView.findViewById(R.id.deleteButton)
    }
}