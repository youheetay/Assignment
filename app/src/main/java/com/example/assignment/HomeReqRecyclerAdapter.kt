package com.example.assignment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeReqRecyclerAdapter  (private val context : Context, private val foodReqList: ArrayList<FoodR>): RecyclerView.Adapter<HomeReqRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeReqRecyclerAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reqlist_item,parent,false)
        val buttonOpenDialog = itemView.findViewById<Button>(R.id.donateBtn)
        buttonOpenDialog.setOnClickListener {
            showConfirmationDialog()
        }
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

    public fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(context)

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.donate_dialog, null) // Create a custom dialog layout

        val quantityPicker = dialogView.findViewById<NumberPicker>(R.id.numberPicker)
        quantityPicker.maxValue = 60
        quantityPicker.minValue = 1

        builder.setView(dialogView)

            .setPositiveButton("Donate") { dialogInterface: DialogInterface, _: Int ->
                val selectedQuantity = quantityPicker.value
                // Handle the positive button click with the selectedQuantity
                dialogInterface.dismiss() // Close the dialog
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                // Handle the negative button click (e.g., cancel the action)
                dialogInterface.dismiss() // Close the dialog
            }

        val dialog: AlertDialog = builder.create()

        // Show the dialog
        dialog.show()
    }
}