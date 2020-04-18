package com.example.newsapp;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class HeadlinesFragment extends Fragment {


    public HeadlinesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_headlines, container, false);

        TabLayout tabLayout = view.findViewById(R.id.headlines_tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("WORLD"));
        tabLayout.addTab(tabLayout.newTab().setText("BUSINESS"));
        tabLayout.addTab(tabLayout.newTab().setText("POLITICS"));
        tabLayout.addTab(tabLayout.newTab().setText("SPORTS"));
        tabLayout.addTab(tabLayout.newTab().setText("TECHNOLOGY"));
        tabLayout.addTab(tabLayout.newTab().setText("SCIENCE"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setOverScrollMode(TabLayout.OVER_SCROLL_IF_CONTENT_SCROLLS);

        final ViewPager viewPager = view.findViewById(R.id.headlines_view_pager);
        final PagerAdapter pagerAdapter = new PagerAdapter(MainActivity.fragmentManager, tabLayout.getTabCount(), getActivity().getApplicationContext());

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        return view;
    }

}
