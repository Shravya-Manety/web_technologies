package com.example.newsapp;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScienceFragment extends Fragment {

    private Context context;
    private SwipeRefreshLayout swipeToRefresh;
    private NewsCardFragment newsCardFragment;
    public ScienceFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_science, container, false);
        newsCardFragment = new NewsCardFragment(context);
        Bundle bundle = new Bundle();
        bundle.putString("newsUrl", "http://localhost:5000/guardianScience");
        bundle.putString("parent", "MainActivity");
        newsCardFragment.setArguments(bundle);
        MainActivity.fragmentManager.beginTransaction().add(R.id.science_fragment, newsCardFragment, null).commit();

        swipeToRefresh = view.findViewById(R.id.news_swipeRefresh_science);
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

}
