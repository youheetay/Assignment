package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.assignment.fragments.AdminDonorFragment;
import com.example.assignment.fragments.AdminRequestFragment;
import com.example.assignment.fragments.DonorFragment;
import com.example.assignment.fragments.RequestorFragment;

public class HomeViewPagerAdapter extends FragmentStateAdapter {
    public HomeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new RequestorFragment();
            case 1:
                return new DonorFragment();
            default:
                return new RequestorFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
