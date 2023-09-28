package com.example.assignment

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
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
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment.fragments.DonorFoodUpdateFragment
import com.example.assignment.fragments.UpdateDonorScreenFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import org.w3c.dom.Text

class HistoryAdapter(private val context: Context, private val foodList: ArrayList<Food>, private val fragmentManager: FragmentManager): RecyclerView.Adapter<HistoryAdapter.ViewHolderHistory>() {

    companion object {
        const val ARG_FOOD = "food"
        const val STORAGE_PERMISSION_CODE = 1 // You can use any unique integer value here
    }

    // Declare variables to store the selected image Uri and ImageView
    private var uri: Uri? = null
    private var image: ImageView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolderHistory {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return ViewHolderHistory(itemView)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.ViewHolderHistory, position: Int) {
        val food : Food = foodList[position]
        holder.foodName.text = food.foodName
        holder.foodDes.text = food.foodDes
        holder.quantity.text = food.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(food.image) // Use the image URL from the Food object
            .into(holder.foodImage)


        holder.editBtn.setOnClickListener {
            val foodEdit = foodList[position]
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.activity_edit_page_dialog, null)
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Update Food")

            val editFoodName = dialogView.findViewById<EditText>(R.id.editFoodName)
            val editFoodDes = dialogView.findViewById<EditText>(R.id.editDes)
            val showQuantity = dialogView.findViewById<TextView>(R.id.quantityDonar)

            val editQuantity = dialogView.findViewById<NumberPicker>(R.id.editQuantity)
            editQuantity.maxValue = 60
            editQuantity.minValue = 1
            editQuantity.wrapSelectorWheel = true
            editQuantity.setOnValueChangedListener { _, _, newValue ->
                showQuantity.text = "Quantity : $newValue"
            }

            editFoodName.setText(foodEdit.foodName)
            editFoodDes.setText(foodEdit.foodDes)
            editQuantity.value = food.quantity.toString().toInt()

            alertDialogBuilder.setView(dialogView)

            alertDialogBuilder.setPositiveButton("Update") { _, _ ->
                val newName = editFoodName.text.toString()
                val newDes = editFoodDes.text.toString()
                val newQuantity = editQuantity.value
                val db = FirebaseFirestore.getInstance()

                val foodId = foodEdit.id

                if (foodId != null) {
                    val updatedData = mapOf(
                        "foodName" to newName,
                        "foodDes" to newDes,
                        "quantity" to newQuantity
                    )

                    db.collection("food").document(foodId).set(updatedData, SetOptions.merge())
                        .addOnSuccessListener {
                            foodEdit.foodName = newName
                            foodEdit.foodDes = newDes
                            foodEdit.quantity = newQuantity
                            notifyItemChanged(position)
                            Toast.makeText(holder.itemView.context, "Update Success", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(holder.itemView.context, "Error updating food: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            // Initialize the image and URI variables here
            image = dialogView.findViewById(R.id.imageView)
            uri?.let { image?.setImageURI(it) }

//            // Set up image selection logic
//            val galleryImage = (context as AppCompatActivity).registerForActivityResult(
//                ActivityResultContracts.GetContent(),
//                ActivityResultCallback { resultUri ->
//                    uri = resultUri
//                    image?.setImageURI(resultUri)
//                })
//
//            dialogView.findViewById<Button>(R.id.browseBtn).setOnClickListener {
//                requestStoragePermission(context as Activity)
//                galleryImage.launch("image/*")
//            }



            alertDialogBuilder.show()
        }


        holder.deleteBtn.setOnClickListener{
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
        val editBtn: Button = itemView.findViewById(R.id.editBtn)
        val deleteBtn: Button = itemView.findViewById(R.id.deleteBtn)
        val documentId: String? = null
        val quantity: TextView = itemView.findViewById(R.id.selectQuantity)
        val foodImage: ImageView = itemView.findViewById(R.id.foodImage)
    }

    private fun requestStoragePermission(activity: Activity) {
        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission hasn't been granted, request it
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
        if (ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            // Permission hasn't been granted, request it
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }



}