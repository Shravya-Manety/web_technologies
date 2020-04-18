package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsCardFragment extends Fragment {


    private List<JSONObject> newsCardList;
    private Context activityContext;
    private BookmarkSharedPreferences bookmarkSharedPreferences;
    private String titleToDisplay = "";
    private String articleId = "";
    private ImageButton alertDialogBookmark;


    public static String EXTRA_NEWS_ARTICLE_ID = "com.example.newsapp.articleid";

    public NewsCardFragment(Context context) {
        this.activityContext = context;
        this.bookmarkSharedPreferences = new BookmarkSharedPreferences(context);
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

        String newsUrl = bundle.getString("newsUrl");
        RequestQueue queue = Volley.newRequestQueue(activityContext);
        StringRequest stringRequestGuardian = new StringRequest(Request.Method.GET, newsUrl,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            newsCardList = new ArrayList<>();
                            if(jsonArray != null){
                                for(int i=0;i<jsonArray.length();i++){
                                    newsCardList.add(jsonArray.getJSONObject(i));
                                }
                                setRecyclerViewAdapter(view);
                            }
                        } catch (JSONException e) {
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
        return view;
    }

    private void setRecyclerViewAdapter(View view) {
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new NewsCardRecyclerViewAdapter(newsCardList, activityContext));

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(activityContext,
                    recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    System.out.println("view id"+view.getId());
                    System.out.println("bookmark button :" + R.id.newscard_bookmark_button);
                    Intent detailedArticleIntent = new Intent(getActivity(), DetailedArticle.class);
                    try {
                        detailedArticleIntent.putExtra(EXTRA_NEWS_ARTICLE_ID, newsCardList.get(position).getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getActivity().startActivity(detailedArticleIntent);
                }

                @Override
                public void onLongClick(final View view, int position) {

                    Toast.makeText(activityContext, "Long press on position :"+position,
                            Toast.LENGTH_LONG).show();
                    final JSONObject newsCardObj = newsCardList.get(position);


                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.long_press_dialog);

                    ImageView newsCardImageView = (ImageView) dialog.findViewById(R.id.alert_dialog_image);
                    TextView newsCardTitle = (TextView) dialog.findViewById(R.id.alert_dialog_title);
                    alertDialogBookmark = (ImageButton) dialog.findViewById(R.id.alert_dialog_bookmark_button);
                    ImageButton alertDialogTwitter = (ImageButton) dialog.findViewById(R.id.alert_dialog_twitter_button);
                    try {
                        articleId = newsCardObj.getString("id");
                        titleToDisplay = newsCardObj.getString("title");
                        newsCardTitle.setText(newsCardObj.getString("title"));
                        Glide.with(activityContext)
                                .load(newsCardObj.getString("image"))
                                .into(newsCardImageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(bookmarkSharedPreferences.containsArticle(articleId)){
                        alertDialogBookmark.setImageResource(R.drawable.bookmark_black_18dp);
                    }
                    else{
                        alertDialogBookmark.setImageResource(R.drawable.bookmark_border_black_18dp);
                    }
                    alertDialogBookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(bookmarkSharedPreferences.containsArticle(articleId)){
                                bookmarkSharedPreferences.removeArticleFromBookmark(articleId, titleToDisplay);
                                alertDialogBookmark.setImageResource(R.drawable.bookmark_border_black_18dp);
                                ImageButton cardBookmarkButton = view.findViewById(R.id.newscard_bookmark_button);
                                cardBookmarkButton.setImageResource(R.drawable.bookmark_border_black_18dp);
                            }
                            else{
                                bookmarkSharedPreferences.addArticleToBookmark(articleId, newsCardObj, titleToDisplay);
                                alertDialogBookmark.setImageResource(R.drawable.bookmark_black_18dp);
                                ImageButton cardBookmarkButton = view.findViewById(R.id.newscard_bookmark_button);
                                cardBookmarkButton.setImageResource(R.drawable.bookmark_black_18dp);
                            }
                        }
                    });
                    alertDialogTwitter.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            Toast.makeText(getActivity(), "tweeted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog.show();
                }
            }));
        }
    }

    public interface ClickListener{
        void onClick(View view,int position);
        void onLongClick(View view,int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener newsCardClickListener;
        private GestureDetector newsCardGestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.newsCardClickListener =clicklistener;
            newsCardGestureDetector =new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && newsCardClickListener !=null && newsCardGestureDetector.onTouchEvent(e)){
                newsCardClickListener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
