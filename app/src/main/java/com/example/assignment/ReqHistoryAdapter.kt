package com.example.assignment

import android.app.AlertDialog
import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ReqHistoryAdapter (private val reqFoodList: ArrayList<FoodR> ):
    RecyclerView.Adapter<ReqHistoryAdapter.ViewHolderHistory>() {

    private lateinit var imageView: ImageView

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

//        holder.editBtn.setOnClickListener{
//
//        }
//        holder.deleteBtn.setOnClickListener{
//            val positionDelete = holder.adapterPosition
//            val deleteFood = reqFoodList[positionDelete]
//
//            // Show a confirmation dialog
//            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
//            alertDialogBuilder.setTitle("Delete Food")
//            alertDialogBuilder.setMessage("Confirm To Delete?")
//            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
//                // Remove the notification from the list locally
//                reqFoodList.remove(deleteFood)
//                notifyItemRemoved(positionDelete)
//
//                // Delete the notification from Firestore
//                val db = FirebaseFirestore.getInstance()
//                val food = db.collection("foodR")
//
//                // Use the correct document ID to delete the specific notification in Firestore
//                val deleteFoodId = deleteFood.id// Assuming id is the correct document ID
//                if (deleteFoodId != null) {
//                    food.document(deleteFoodId)
//                        .delete()
//                        .addOnSuccessListener {
//                            // change to snakbar
//                            Toast.makeText(holder.itemView.context, "You have deleted successfully", Toast.LENGTH_SHORT).show()
//
//                        }
//                        .addOnFailureListener { exception ->
//                            Log.e(ContentValues.TAG, "Error deleting Food from Firestore: $exception")
//                            Toast.makeText(holder.itemView.context, "You have deleted successfully", Toast.LENGTH_SHORT).show()
//                        }
//                }
//            }
//            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            alertDialogBuilder.show()
//        }
    }


    override fun getItemCount(): Int {
        return reqFoodList.size
    }

    public class ViewHolderHistory(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodNameR : TextView = itemView.findViewById(R.id.foodNameReq)
        val foodDesR : TextView = itemView.findViewById(R.id.foodDesR)
        val quantity : TextView = itemView.findViewById(R.id.quantityReq)
        val image: ImageView = itemView.findViewById(R.id.imageView2)
//        val editBtn: Button = itemView.findViewById(R.id.editBtn)
//        val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)
        val documentId: String? = null

    }
}