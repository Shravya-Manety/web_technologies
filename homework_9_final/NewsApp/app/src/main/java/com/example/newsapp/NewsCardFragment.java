package com.example.newsapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.newsapp.Utilities.TIMEOUT_MS;

public class NewsCardFragment extends Fragment {


    private List<JSONObject> newsCardList = new ArrayList<>();;
    private Context activityContext;
    private String parentClassName;
    private RelativeLayout progressBarLayout;
    private String newsUrl;
    private RequestQueue queue;
    RecyclerView recyclerView;
    NewsCardRecyclerViewAdapter adapter;

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
        JsonArrayRequest jsonArrayRequestGuardian = new JsonArrayRequest(Request.Method.GET, newsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        newsCardList.add((JSONObject) response.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setRecyclerViewAdapter(view);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("NewsCardFragment", error.toString());
            }
        });
        jsonArrayRequestGuardian.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonArrayRequestGuardian);
        return view;

    }

    public void resetAdapterOnRefresh(){
        adapter = (NewsCardRecyclerViewAdapter)recyclerView.getAdapter();
        adapter.clear();
        JsonArrayRequest jsonArrayRequestGuardian = new JsonArrayRequest(Request.Method.GET, newsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                newsCardList = new ArrayList<>();
                for(int i=0;i<response.length();i++){
                    try {
                        newsCardList.add(response.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.addAll(newsCardList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("NewsCardFragment", error.toString());
            }
        });

        queue.add(jsonArrayRequestGuardian);
    }

    private void setRecyclerViewAdapter(View view) {
        progressBarLayout.setVisibility(View.GONE);
        if(parentClassName.equalsIgnoreCase("SearchActivity") && newsCardList.size() == 0) {
            SearchActivity.toDisplayResults();
        }
        assignAdapter();
    }

    private void assignAdapter() {
        adapter = new NewsCardRecyclerViewAdapter(newsCardList,parentClassName, new ClickListener() {
            @Override
            public void onPositionClicked(int position, boolean isFacebook) throws JSONException {
                if(isFacebook){
                    callFacebookIntent(newsCardList.get(position).getString("link"));
                }
            }
            @Override
            public void onLongClicked(int position) {
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void resetAdapter(){
        if(recyclerView != null){
            assignAdapter();
        }
    }

    public void callFacebookIntent(String newsUrl){
        final ShareDialog shareDialog = new ShareDialog(this);
        ShareHashtag shareHashtag = new ShareHashtag.Builder()
                .setHashtag("CSCI571")
                .build();

        if (shareDialog.canShow(ShareLinkContent.class)) {

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setQuote("CSCI571 NewsApp")
                    .setContentUrl(Uri.parse(newsUrl))
                    .setShareHashtag(shareHashtag)
                    .build();

            shareDialog.show(linkContent);
        }
    }
}
