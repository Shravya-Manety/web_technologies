package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SearchActivity extends AppCompatActivity {

    private TextView searchResultsTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.search_page_toolbar);

        System.out.println("Inside SearchActivity");

        if(findViewById(R.id.search_results_fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }
            System.out.println("found search_results fragment container");
            searchResultsTitle = findViewById(R.id.search_results_title);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Intent searchIntent = getIntent();
            String queryKeyword = searchIntent.getStringExtra("QueryKeyword");

            System.out.println("Inside SearchActivity Query word :"+queryKeyword);

            searchResultsTitle.setText("Search Results for " + queryKeyword);

            NewsCardFragment newsCardFragment = new NewsCardFragment(this);
            Bundle bundleNewsFragment = new Bundle();
            bundleNewsFragment.putString("newsUrl", "http://localhost:5000/guardianSearch/"+queryKeyword);
            newsCardFragment.setArguments(bundleNewsFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.search_results_fragment_container, newsCardFragment, null).commit();
//            getSearchResults();
        }

    }

    private void getSearchResults() {

    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        getSearchResults();
//
//    }
}
