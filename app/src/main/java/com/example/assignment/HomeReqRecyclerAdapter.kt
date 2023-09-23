package com.example.assignment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeReqRecyclerAdapter  (private val foodReqList: ArrayList<FoodR>): RecyclerView.Adapter<HomeReqRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeReqRecyclerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reqlist_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeReqRecyclerAdapter.MyViewHolder, position: Int) {
        val foodR : FoodR = foodReqList[position]
        holder.foodNameReq.text = foodR.foodNameR
        holder.foodDesReq.text = foodR.foodDesR
        holder.quantity.text = foodR.quantity.toString()
    }

    override fun getItemCount(): Int {
        return foodReqList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val foodNameReq : TextView = itemView.findViewById(R.id.foodNameReq)
        val foodDesReq : TextView = itemView.findViewById(R.id.foodDesR)
        val quantity : TextView = itemView.findViewById(R.id.quantityReq)
    }
}