package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.assignment.fragments.AdminDonorPendingFragment
import com.example.assignment.fragments.AdminPendingFragment


class emptyActivityDonor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty_donor)

        makeCurrentFragment(AdminDonorPendingFragment())

    }

    private fun makeCurrentFragment(fragment: Fragment){
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.emptyViewDonor, fragment)
                commit() // Commit the transaction to apply the fragment replacement
            }

    }
}