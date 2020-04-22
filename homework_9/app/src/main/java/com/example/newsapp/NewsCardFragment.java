package com.example.newsapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.newsapp.Utilities.TIMEOUT_MS;

public class NewsCardFragment extends Fragment {


    private List<JSONObject> newsCardList;
    private Context activityContext;
    private String parentClassName;
    private RelativeLayout progressBarLayout;
    private String newsUrl;
    private RequestQueue queue;
    RecyclerView recyclerView;

    public NewsCardFragment(Context context) {
        this.activityContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        final View view = inflater.inflate(R.layout.fragment_newscard_list, container, false);

        newsUrl = bundle.getString("newsUrl");
        parentClassName = bundle.getString("parent");
        progressBarLayout = (RelativeLayout) view.findViewById(R.id.progressbar_view);
        progressBarLayout.setVisibility(View.VISIBLE);
        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.newscard_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        queue = Volley.newRequestQueue(activityContext);
        StringRequest stringRequestGuardian = new StringRequest(Request.Method.GET, newsUrl,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        try {
                            System.out.println("Response from API" + response);
                            JSONArray jsonArray = new JSONArray(response);
                            newsCardList = new ArrayList<>();
                            if(jsonArray != null){
                                for(int i=0;i<jsonArray.length();i++){
                                    newsCardList.add(jsonArray.getJSONObject(i));
                                }
                                setRecyclerViewAdapter(view);
                            }
                        } catch (JSONException e) {
                            System.out.println("Error from newscard fragment -------------------*****************---------------------");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("NewsCardFragment", error.toString());
            }
        });
        stringRequestGuardian.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequestGuardian);
        System.out.println("the view is returned");
        return view;

    }

    public void resetAdapterOnRefresh(){
        System.out.println("This method is called ----------------------------------------------------------------------");
        final NewsCardRecyclerViewAdapter adapter = (NewsCardRecyclerViewAdapter)recyclerView.getAdapter();
        adapter.clear();
        StringRequest stringRequestGuardian = new StringRequest(Request.Method.GET, newsUrl,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        try {
                            System.out.println("Response from API" + response);
                            JSONArray jsonArray = new JSONArray(response);
                            newsCardList = new ArrayList<>();
                            if(jsonArray != null){
                                for(int i=0;i<jsonArray.length();i++){
                                    newsCardList.add(jsonArray.getJSONObject(i));
                                }
                                adapter.addAll(newsCardList);
                                System.out.println("adapter is set again ------------------------------------");
                            }
                        } catch (JSONException e) {
                            System.out.println("Error from newscard fragment -------------------*****************---------------------");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("NewsCardFragment", error.toString());
            }
        });
        queue.add(stringRequestGuardian);
    }

    private void setRecyclerViewAdapter(View view) {
        // Set the adapter
        System.out.println("Newscard fragment setting recycler view adapter---------------------------------------");
        progressBarLayout.setVisibility(View.GONE);
        if(parentClassName.equalsIgnoreCase("SearchActivity") && newsCardList.size() == 0){
            SearchActivity.toDisplayResults();
        }


        NewsCardRecyclerViewAdapter adapter = new NewsCardRecyclerViewAdapter(newsCardList,parentClassName, new com.example.newsapp.ClickListener() {
            @Override
            public void onPositionClicked(int position) {
                System.out.println("Finally either bookmarked or went to detail page for article at position :"+position);
            }
            @Override
            public void onLongClicked(int position) {
                System.out.println("showed alert dialog for article at position :"+position);
            }
        });
        System.out.println("Recycler view adapter set");
        recyclerView.setAdapter(adapter);
    }
}
