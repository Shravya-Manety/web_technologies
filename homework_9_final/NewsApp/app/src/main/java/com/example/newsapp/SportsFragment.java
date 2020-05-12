package com.example.newsapp;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.example.newsapp.Utilities.BACKEND_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class SportsFragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout swipeToRefresh;
    private NewsCardFragment newsCardFragment;
    public SportsFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sports, container, false);
        newsCardFragment = new NewsCardFragment(context);
        Bundle bundle = new Bundle();
        bundle.putString("newsUrl", BACKEND_URL + "guardianSports");
        bundle.putString("parent", "MainActivity");
        newsCardFragment.setArguments(bundle);
        MainActivity.fragmentManager.beginTransaction().add(R.id.sports_fragment, newsCardFragment, null).commit();

        swipeToRefresh = view.findViewById(R.id.news_swipeRefresh_sports);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsCardFragment.resetAdapterOnRefresh();
                final Handler animationHandler = new Handler();
                animationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeToRefresh.isRefreshing()) {
                            swipeToRefresh.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        newsCardFragment.resetAdapter();
    }
}
