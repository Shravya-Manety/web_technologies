package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import android.Manifest;
import android.view.View;
import android.widget.AdapterView;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locationManager;
    String provider;
    Double latitude = null,longitude = null;
    public static FragmentManager fragmentManager;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);

        if(findViewById(R.id.navigation) != null){
            if(savedInstanceState != null){
                System.out.println("SavedInstanceState = "+savedInstanceState);
                System.out.println("In saved instance: returning from here");
                return;
            }
            JodaTimeAndroid.init(this);
            fragmentManager = getSupportFragmentManager();
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(new Criteria(), false);
            System.out.println("Provider : "+ provider);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.home_fragment:
                    selectedFragment = new HomeFragment();
                    if(latitude != null && longitude != null){
                        Bundle bundle = new Bundle();
                        bundle.putString("latitude" , latitude.toString());
                        bundle.putString("longitude", longitude.toString());
                        selectedFragment.setArguments(bundle);
                    }
                    break;
                case R.id.headlines_fragment:
                    selectedFragment = new HeadlinesFragment();
                    break;
                case R.id.trend_fragment:
                    selectedFragment = new TrendingFragment();
                    break;
                case R.id.bookmarks_fragment:
                    selectedFragment = new BookmarksFragment();
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("bottom nav item").commit();
            return true;
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        System.out.println("OncreateOptionsMenu");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_bar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);


        SearchView searchView = (SearchView) searchItem.getActionView();


        // Get SearchView autocomplete object.
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        autoSuggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setThreshold(3);
        searchAutoComplete.setAdapter(autoSuggestAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchAutoComplete.setText("" + (String) autoSuggestAdapter.getObject(position));
            }
        });

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchActivityIntent = new Intent(MainActivity.this, SearchActivity.class);
                searchActivityIntent.putExtra("QueryKeyword", query);
                MainActivity.this.startActivity(searchActivityIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void makeApiCall(String newText) {
        final List<String> autocompleteSuggestions = new ArrayList<String>();
        String bingAutoSuggestAPICall = "https://api.cognitive.microsoft.com/bing/v7.0/suggestions?q=" + newText;
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest bingAutoSuggestRequest = new JsonObjectRequest(Request.Method.GET, bingAutoSuggestAPICall, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject suggestionGroups = (JSONObject) response.getJSONArray("suggestionGroups").get(0);
                            JSONArray searchSuggestions = suggestionGroups.getJSONArray("searchSuggestions");
                            int i = 0;
                            while (i < searchSuggestions.length() && i < 5) {
                                JSONObject suggestion = (JSONObject) searchSuggestions.get(i);
                                autocompleteSuggestions.add(suggestion.getString("displayText"));
                                i++;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        autoSuggestAdapter.setData(autocompleteSuggestions);
                        autoSuggestAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", "3a8f2ea9fbd64bb6951fe91b149d409b");
                return params;
            }
        };
        queue.add(bingAutoSuggestRequest);
    }
    }
