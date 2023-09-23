package com.example.assignment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
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
        myViewPagerAdapter =  MyViewPagerAdapter(this)
        viewPager2.setAdapter(myViewPagerAdapter)

        //tabLayout.setupWithViewPager(viewPager2)

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
}