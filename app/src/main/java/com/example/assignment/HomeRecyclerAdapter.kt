package com.example.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HomeRecyclerAdapter (private val foodList: ArrayList<Food>): RecyclerView.Adapter<HomeRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeRecyclerAdapter.MyViewHolder, position: Int) {
        val food : Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes
        holder.quantity.text = food.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(food.image) // Use the image URL from the Food object
            .into(holder.foodImage)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodName : TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDes : TextView = itemView.findViewById(R.id.tvFoodDes)
        val quantity: TextView = itemView.findViewById(R.id.selectQuantity)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)

    }
}