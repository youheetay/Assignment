package com.example.assignment

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HistoryAdapter (private val foodList: ArrayList<Food>): RecyclerView.Adapter<HistoryAdapter.ViewHolderHistory>() {

    private lateinit var imageView: ImageView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolderHistory {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return ViewHolderHistory(itemView)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolderHistory, position: Int) {
        val food : Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes

        holder.editImage.setOnClickListener{

        }
        holder.deleteImage.setOnClickListener{
            foodList.removeAt(position)
            notifyItemRemoved(position)
            Toast.makeText(holder.itemView.context, "Delete clicked for ${food.foodName}", Toast.LENGTH_SHORT).show()
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