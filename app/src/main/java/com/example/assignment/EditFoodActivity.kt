package com.example.assignment

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class EditFoodActivity : AppCompatActivity() {

    private lateinit var editTextFoodName: EditText
    private lateinit var editTextFoodDescription: EditText
    private lateinit var updateBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_food)

        editTextFoodName = findViewById(R.id.editTextFoodName)
        editTextFoodDescription = findViewById(R.id.editTextFoodDescription)
        updateBtn = findViewById(R.id.updateBtn)

        updateBtn.setOnClickListener {
            // Get the edited data from UI elements
            val editedFoodName = editTextFoodName.text.toString()
            val editedFoodDescription = editTextFoodDescription.text.toString()

            // Create a Food object with the edited data
            val editedFood = Food(editedFoodName, editedFoodDescription)

            // Create the result intent
            val resultIntent = Intent()
            resultIntent.putExtra("editedFood", editedFood)

            // Set the result to RESULT_OK and send back the edited data
            setResult(Activity.RESULT_OK, resultIntent)

            // Finish the activity
            finish()
        }



    }
}