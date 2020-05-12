package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import static com.example.newsapp.Utilities.BACKEND_URL;

public class SearchActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeToRefresh;
    private NewsCardFragment newsCardFragment;
    private static TextView no_search_results;
    private static FrameLayout search_results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.search_page_toolbar);
        no_search_results = findViewById(R.id.no_search_results);
        search_results = findViewById(R.id.search_results_fragment_container);

        if(findViewById(R.id.search_results_fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Intent searchIntent = getIntent();
            String queryKeyword = searchIntent.getStringExtra("QueryKeyword");

            getSupportActionBar().setTitle("Search Results for " + queryKeyword);

            newsCardFragment = new NewsCardFragment(this);
            Bundle bundleNewsFragment = new Bundle();
            bundleNewsFragment.putString("newsUrl", BACKEND_URL + "guardianSearch/"+queryKeyword);
            bundleNewsFragment.putString("parent", "SearchActivity");
            newsCardFragment.setArguments(bundleNewsFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.search_results_fragment_container, newsCardFragment, null).commit();

            swipeToRefresh = findViewById(R.id.news_swipeRefresh_search);
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
                    }, 1500);
                }
            });
        }
    }
    @Override
    public Intent getSupportParentActivityIntent(){
        return getParentIntentImplementation();
    }
    @Override
    public Intent getParentActivityIntent(){
        return getParentIntentImplementation();
    }

    private Intent getParentIntentImplementation() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public static void toDisplayResults(){
        search_results.setVisibility(View.GONE);
        no_search_results.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        newsCardFragment.resetAdapter();
    }
}
