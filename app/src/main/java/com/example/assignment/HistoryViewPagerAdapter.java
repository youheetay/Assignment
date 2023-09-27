package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.assignment.fragments.AdminDonorFragment;
import com.example.assignment.fragments.AdminRequestFragment;
import com.example.assignment.fragments.DonorFragment;
import com.example.assignment.fragments.HistoryFragment;
import com.example.assignment.fragments.ReqHistoryFragment;
import com.example.assignment.fragments.RequestorFragment;

public class HistoryViewPagerAdapter extends FragmentStateAdapter {
    public HistoryViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new ReqHistoryFragment();
            case 1:
                return new HistoryFragment();
            default:
                return new ReqHistoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
