package com.example.assignment

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.assignment.R.id.tab_layout_Home
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class HistoryViewActivity : AppCompatActivity() {

    private lateinit var tabLayout1 : TabLayout
    private lateinit var viewPagerHome : ViewPager2
    private lateinit var historyViewPagerAdapter : HistoryViewPagerAdapter
    // Declare variables to store the selected image Uri and ImageView
    private var uri: Uri? = null
    private var image: ImageView? = null

    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_view)

        tabLayout1 = findViewById(R.id.tab_layout_HistoryView)
        viewPagerHome = findViewById(R.id.historyView_pager)
        historyViewPagerAdapter =  HistoryViewPagerAdapter(this)
        viewPagerHome.setAdapter(historyViewPagerAdapter)

        backButton = findViewById(R.id.backButton)

        //tabLayout.setupWithViewPager(viewPager2)

        tabLayout1.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

            override fun onTabSelected(tab: TabLayout.Tab){
                viewPagerHome.setCurrentItem(tab.getPosition())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout1.getTabAt(position)?.select()
            }
        })

        backButton.setOnClickListener {
            onBackPressed() // Call onBackPressed to navigate back
        }
    }


}