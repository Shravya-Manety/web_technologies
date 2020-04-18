package com.example.newsapp;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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
    public ScienceFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_science, container, false);
        NewsCardFragment newsCardFragment = new NewsCardFragment(context);
        Bundle bundle = new Bundle();
        bundle.putString("newsUrl", "http://localhost:5000/guardianScience");
        newsCardFragment.setArguments(bundle);
        MainActivity.fragmentManager.beginTransaction().add(R.id.science_fragment, newsCardFragment, null).commit();
        return view;
    }

}
