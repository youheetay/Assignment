package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.assignment.fragments.AdminDonorFragment;
import com.example.assignment.fragments.AdminRequestFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new AdminRequestFragment();
            case 1:
                return new AdminDonorFragment();
            default:
                return new AdminRequestFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
