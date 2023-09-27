package com.example.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeRecyclerAdapter2 (private val foodList: ArrayList<Food>): RecyclerView.Adapter<HomeRecyclerAdapter2.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerAdapter2.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item2,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeRecyclerAdapter2.MyViewHolder, position: Int) {
        val food : Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodName : TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDes : TextView = itemView.findViewById(R.id.tvFoodDes)

    }
}