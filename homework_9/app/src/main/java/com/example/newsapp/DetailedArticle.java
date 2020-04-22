package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.newsapp.Utilities.EXTRA_NEWS_ARTICLE_ID;
import static com.example.newsapp.Utilities.PARENT_CLASS_NAME;

public class DetailedArticle extends AppCompatActivity {

    private String articleId;
    private String articleTitle;
    private TextView toolbarTitle;
    private TextView detailedArticleTitle;
    private TextView detailedArticleDate;
    private TextView detailedArticleSection;
    private TextView detailedArticleDescription;
    private ImageView detailedArticleImage;
    private TextView detailedArticleLink;
    private Toolbar toolbar;
    private ImageButton bookmarkButton, twitterButton;
    private BookmarkSharedPreferences bookmarkSharedPreferences;
    private JSONObject detailedNews;
    private String parentClassName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_article);
        toolbar = (Toolbar)findViewById(R.id.detailed_page_toolbar);

        Intent intent = getIntent();
        articleId = intent.getStringExtra(EXTRA_NEWS_ARTICLE_ID);
        parentClassName = intent.getStringExtra(PARENT_CLASS_NAME);
        bookmarkSharedPreferences = new BookmarkSharedPreferences(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        String detailedArticleUrl = "http://localhost:5000/guardianDetail/"+articleId;
        Log.d("DetailedArticleActivity", detailedArticleUrl);
        StringRequest stringRequestGuardianDetail = new StringRequest(Request.Method.GET, detailedArticleUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            processResponseGuardianDetail(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Response :" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("DetailedArticleActivity", error.toString());
            }
        });
        queue.add(stringRequestGuardianDetail);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void processResponseGuardianDetail(String response) throws JSONException {
        detailedNews = (JSONObject)new JSONArray(response).get(0);
        detailedNews.put("id", articleId);
        toolbarTitle = findViewById(R.id.page_title);
        toolbarTitle.setText(detailedNews.getString("title"));



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        articleTitle = detailedNews.getString("title");

        detailedArticleImage = findViewById(R.id.detailed_article_image);
        Glide.with(this)
                .load(detailedNews.getString("image"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(detailedArticleImage);

        detailedArticleTitle = findViewById(R.id.detailed_article_title);
        detailedArticleTitle.setText(articleTitle);

        detailedArticleSection = findViewById(R.id.detailed_article_section);
        detailedArticleSection.setText(detailedNews.getString("section"));

        detailedArticleDate = findViewById(R.id.detailed_article_date);
        String formattedDate = Utilities.getArticleDate(detailedNews.getString("date"), "detailed");
        detailedArticleDate.setText(formattedDate);

        detailedArticleDescription = findViewById(R.id.detailed_article_description);
        detailedArticleDescription.setText(HtmlCompat.fromHtml(detailedNews.getString("description"), 0));

        detailedArticleLink = findViewById(R.id.detailed_article_link);
        detailedArticleLink.setClickable(true);
        detailedArticleLink.setMovementMethod(LinkMovementMethod.getInstance());
        String newsUrl = detailedNews.getString("link");
        String newsLink = "<a href = '" + newsUrl + "'><u>View Full Article</u></a>";
        detailedArticleLink.setText(HtmlCompat.fromHtml(newsLink, 0));

        bookmarkButton = findViewById(R.id.detailed_bookmark_button);
        if(bookmarkSharedPreferences.containsArticle(articleId)){
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_black);
            bookmarkButton.setMinimumHeight(50);
            bookmarkButton.setMinimumWidth(50);
        }
        else{
            bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_black);
            bookmarkButton.setMinimumHeight(50);
            bookmarkButton.setMinimumWidth(50);
        }
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarkSharedPreferences.containsArticle(articleId)){
                    bookmarkSharedPreferences.removeArticleFromBookmark(articleId, articleTitle);
                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_black);
                    bookmarkButton.setMinimumHeight(50);
                    bookmarkButton.setMinimumWidth(50);
                }
                else{
                    bookmarkSharedPreferences.addArticleToBookmark(articleId, detailedNews, articleTitle);
                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_black);
                    bookmarkButton.setMinimumHeight(50);
                    bookmarkButton.setMinimumWidth(50);
                }
            }
        });
        twitterButton = findViewById(R.id.detailed_twitter_button);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String twitterLink = null;
                try {
                    twitterLink = detailedNews.getString("link");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String twitterUrl = "https://twitter.com/intent/tweet?text=Check%20out%20this%20Link:&url="+twitterLink+"&hashtags=CSCI571NewsSearch";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl)));
            }
        });
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
        Intent intent = null;
        if(parentClassName.equalsIgnoreCase("MainActivity")){
            System.out.println("Going back to main activity");

            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        else{
            intent = new Intent(this, SearchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        return intent;
    }


}
