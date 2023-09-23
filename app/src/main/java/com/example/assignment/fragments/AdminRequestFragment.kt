package com.example.assignment.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.assignment.AdminAddReq
import com.example.assignment.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore


class AdminRequestFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("adminFoodReq")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_admin_request, container, false)


        rootView.findViewById<FloatingActionButton>(R.id.CreateButton).setOnClickListener{
            val intent = Intent(activity, AdminAddReq::class.java)
            startActivity(intent)
        }

//        rootView.findViewById<FloatingActionButton>(R.id.CreateButton).setOnClickListener {
//            val fragment = AdminAddReqFragment()
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.AdminReq, fragment)
//            transaction.addToBackStack(null)
//
//            transaction.commit()
//            rootView.findViewById<FrameLayout>(R.id.AdminReq).visibility = View.GONE
//        }



        return rootView
    }


}