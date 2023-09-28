package com.example.assignment.Adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Food
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore

class AdminDonorPendingAdapter (private val foodList: ArrayList<Food>) :
    RecyclerView.Adapter<AdminDonorPendingAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminDonorPendingAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pending_list,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminDonorPendingAdapter.MyViewHolder, position: Int) {
        val food : Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes

        holder.approveBtn.setOnClickListener{
            val positionDelete = holder.adapterPosition
            val deleteFoodDonor = foodList[positionDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setMessage("Confirm To Approve?")
            alertDialogBuilder.setPositiveButton("Approve") { _, _ ->
                val db = FirebaseFirestore.getInstance()
                val sourceCollection  = db.collection("foodPendingDonor")
                val targetCollection  = db.collection("food")
                val transferFoodId = deleteFoodDonor.id// Assuming id is the correct document ID

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
                                            foodList.remove(deleteFoodDonor)
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
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodName : TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDes : TextView = itemView.findViewById(R.id.tvFoodDes)
        val approveBtn : Button = itemView.findViewById(R.id.approveBtn)
    }
}