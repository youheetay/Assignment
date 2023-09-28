package com.example.assignment.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Food
import com.example.assignment.R

class HomeRecyclerAdapter (private val foodList: ArrayList<Food>): RecyclerView.Adapter<HomeRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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