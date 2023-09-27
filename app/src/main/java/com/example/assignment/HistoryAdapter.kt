package com.example.assignment

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.fragments.DonorFoodUpdateFragment
import com.example.assignment.fragments.UpdateDonorScreenFragment
import com.google.firebase.firestore.FirebaseFirestore

class HistoryAdapter (private val context: Context, private val foodList: ArrayList<Food>, private val fragmentManager: FragmentManager): RecyclerView.Adapter<HistoryAdapter.ViewHolderHistory>() {

    companion object {
        const val ARG_FOOD = "food"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolderHistory {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return ViewHolderHistory(itemView)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolderHistory, position: Int) {
        val food : Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes

        holder.editImage.setOnClickListener{

            // Handle edit button click here
            // You can open an edit dialog/fragment here
            val foodEdit = foodList[position]

            // Create a new instance of the DonorFoodUpdateFragment
            val editDialogFragment = DonorFoodUpdateFragment()

            // Pass the notification data to the fragment
            val args = Bundle()
            args.putParcelable(ARG_FOOD, foodEdit)
            editDialogFragment.arguments = args

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.admin_fl_wrapper, editDialogFragment)
                .addToBackStack(null)
                .commit()

        }


        holder.deleteImage.setOnClickListener{
            val positionDelete = holder.adapterPosition
            val deleteFood = foodList[positionDelete]

            // Show a confirmation dialog
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Delete Food")
            alertDialogBuilder.setMessage("Confirm To Delete?")
            alertDialogBuilder.setPositiveButton("Delete") { _, _ ->
                // Remove the notification from the list locally
                foodList.remove(deleteFood)
                notifyItemRemoved(positionDelete)

                // Delete the notification from Firestore
                val db = FirebaseFirestore.getInstance()
                val food = db.collection("food")

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
                            Log.e(TAG, "Error deleting Food from Firestore: $exception")
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
        return foodList.size
    }

    public class ViewHolderHistory(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodName : TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDes : TextView = itemView.findViewById(R.id.tvFoodDes)
        val editImage: ImageView = itemView.findViewById(R.id.editBtn)
        val deleteImage: ImageView = itemView.findViewById(R.id.deleteBtn)
        val documentId: String? = null
    }

}