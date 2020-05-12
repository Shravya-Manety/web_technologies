package com.example.newsapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final int numOfTabs;
    private Context context;

    public PagerAdapter(@NonNull FragmentManager fm, int numOfTabs, Context context) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
        this.context = context;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new WorldFragment(context);
            case 1: return new BusinessFragment(context);
            case 2: return new PoliticsFragment(context);
            case 3: return new SportsFragment(context);
            case 4: return new TechnologyFragment(context);
            case 5: return new ScienceFragment(context);
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
