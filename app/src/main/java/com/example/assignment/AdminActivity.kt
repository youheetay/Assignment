package com.example.assignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.example.assignment.Adapter.MyViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab

class AdminActivity : AppCompatActivity() {

private lateinit var tabLayout : TabLayout
private lateinit var viewPager2 : ViewPager2
private lateinit var myViewPagerAdapter : MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager)
        myViewPagerAdapter =
            MyViewPagerAdapter(this)
        viewPager2.setAdapter(myViewPagerAdapter)

        //tabLayout.setupWithViewPager(viewPager2)
        val imageBtn : ImageButton = findViewById(R.id.imageButton)

        imageBtn.setOnClickListener {
            showSignOutConfirmationDialog()
        }


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

            override fun onTabSelected(tab: Tab){
                viewPager2.setCurrentItem(tab.getPosition())
            }

            override fun onTabUnselected(tab: Tab?) {

            }

            override fun onTabReselected(tab: Tab?) {

            }
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }
        })
    }

    private fun showSignOutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Sign Out Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to sign out?")

        // Set up the positive button and its click listener
        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up the negative button and its click listener
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // User clicked No, dismiss the dialog
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}