package com.example.assignment.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.Food
import com.example.assignment.HistoryAdapter.Companion.ARG_FOOD
import com.example.assignment.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class DonorFoodUpdateFragment : Fragment() {

    private lateinit var editFoodName: EditText
    private lateinit var editFoodDes: EditText
    private lateinit var updateBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_donor_food_update, container, false)


        val builder = AlertDialog.Builder(requireActivity())
        val inflater = LayoutInflater.from(requireActivity())

        // Retrieve the notification data from the arguments bundle
        val foodEdit = arguments?.getParcelable(ARG_FOOD) as Food?

        editFoodName = view.findViewById(R.id.editFoodName)
        editFoodDes = view.findViewById(R.id.editDes)
        updateBtn = view.findViewById(R.id.updateBtn)

        // Populate the EditText fields with the existing food data
        foodEdit?.let { food ->
            editFoodName.setText(food.foodName)
            editFoodDes.setText(food.foodDes)
        }

        updateBtn.setOnClickListener {
            // Handle the update button click here
            val updatedFoodName = editFoodName.text.toString()
            val updatedFoodDes = editFoodDes.text.toString()

            // Update the notification data in Firestore
            foodEdit?.let { food ->
                val db = FirebaseFirestore.getInstance()

                val foodMap = hashMapOf(
                    "foodName" to updatedFoodName,
                    "foodDes"  to updatedFoodDes
                )

                // Get the document ID from the food object
                val foodDocumentId = food.id.toString()

                // Update the document with the specified ID using set() with merge option
                db.collection("food").document(foodDocumentId)
                    .set(foodMap, SetOptions.merge())
                    .addOnSuccessListener {
                        // Data updated successfully
                        showSuccessDialog()
                        openFragment(UpdateDonorScreenFragment())
                    }
                    .addOnFailureListener { exception ->
                        // Handle the failure to update data
                        showErrorDialog(exception.message)
                    }
            }
        }



        return view
    }

    private fun showSuccessDialog() {
        val successDialog = AlertDialog.Builder(requireContext())
            .setTitle("Success")
            .setMessage("Food updated successfully.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                // Navigate back to your notification list fragment
                // You can use fragmentManager.popBackStack() or other navigation methods
            }
            .create()

        successDialog.show()
    }

    private fun showErrorDialog(errorMessage: String?) {
        val errorDialog = AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Failed to update FoodList: $errorMessage")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        errorDialog.show()
    }

    private fun openFragment(fragment : Fragment){
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.admin_fl_wrapper, fragment)
        fragmentTransaction.addToBackStack(null) // Optional, to allow back navigation
        fragmentTransaction.commit()
    }

}