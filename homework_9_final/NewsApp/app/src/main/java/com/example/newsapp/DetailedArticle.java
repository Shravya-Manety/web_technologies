package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.newsapp.Utilities.BACKEND_URL;
import static com.example.newsapp.Utilities.EXTRA_NEWS_ARTICLE_ID;
import static com.example.newsapp.Utilities.PARENT_CLASS_NAME;
import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

public class DetailedArticle extends AppCompatActivity {

    private String articleId, articleTitle;
    private TextView detailedArticleTitle, detailedArticleDate, detailedArticleSection, detailedArticleDescription, detailedArticleLink;
    private ImageView detailedArticleImage;
    private Toolbar toolbar;
    private BookmarkSharedPreferences bookmarkSharedPreferences;
    private JSONObject detailedNews;
    private String parentClassName, newsUrl;
    private RelativeLayout progressBarLayout;
    private CardView cardView;
    private ImageButton bookmarkButton, shareButton, twitterButton, facebookButton, emailButton;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_article);

        context = getApplicationContext();
        toolbar = (Toolbar)findViewById(R.id.detailed_page_toolbar);

        Intent intent = getIntent();
        articleId = intent.getStringExtra(EXTRA_NEWS_ARTICLE_ID);
        parentClassName = intent.getStringExtra(PARENT_CLASS_NAME);
        bookmarkSharedPreferences = new BookmarkSharedPreferences(this);

        progressBarLayout = (RelativeLayout) findViewById(R.id.progressbar_view);
        progressBarLayout.setVisibility(View.VISIBLE);
        cardView = (CardView) findViewById(R.id.detailed_page_card);

        RequestQueue queue = Volley.newRequestQueue(this);
        String detailedArticleUrl = BACKEND_URL + "guardianDetail/"+articleId;
        Log.d("DetailedArticleActivity", detailedArticleUrl);
        JsonArrayRequest jsonArrayRequestGuardianDetail = new JsonArrayRequest(Request.Method.GET, detailedArticleUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    processResponseGuardianDetail(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("DetailedArticleActivity", error.toString());
            }
        });
        queue.add(jsonArrayRequestGuardianDetail);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void processResponseGuardianDetail(JSONArray response) throws JSONException {

        progressBarLayout.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);

        detailedNews = (JSONObject)response.get(0);
        detailedNews.put("id", articleId);
        articleTitle = detailedNews.getString("title");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(articleTitle);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailedArticleImage = findViewById(R.id.detailed_article_image);
        Glide.with(this)
                .load(detailedNews.getString("image"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(detailedArticleImage);

        detailedArticleTitle = findViewById(R.id.detailed_article_title);
        detailedArticleTitle.setText(articleTitle);
        detailedArticleDate = findViewById(R.id.detailed_article_date);
        String formattedDate = Utilities.getArticleDate(detailedNews.getString("date"), "detailed");
        detailedArticleDate.setText(formattedDate);

        detailedArticleDescription = findViewById(R.id.detailed_article_description);
        detailedArticleDescription.setText(HtmlCompat.fromHtml(detailedNews.getString("description"), 0));
        detailedArticleDescription.setMaxLines(27);
        detailedArticleDescription.setEllipsize(TextUtils.TruncateAt.END);

        detailedArticleLink = findViewById(R.id.detailed_article_link);
        detailedArticleLink.setClickable(true);
        detailedArticleLink.setMovementMethod(LinkMovementMethod.getInstance());
        newsUrl = detailedNews.getString("link");
        final String newsLink = "<a href = '" + newsUrl + "'><u>View Full Article</u></a>";
        detailedArticleLink.setText(HtmlCompat.fromHtml(newsLink, 0));

        final FloatingActionButton fab = findViewById(R.id.bookmark_fab);
        if(bookmarkSharedPreferences.containsArticle(articleId)){
            Drawable drawable = setTint(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_black), R.color.colorPrimary);
            fab.setImageDrawable(drawable);
        }
        else{
            Drawable drawable = setTint(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border_black), R.color.colorPrimary);
            fab.setImageDrawable(drawable);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bookmarkSharedPreferences.containsArticle(articleId)){
                    bookmarkSharedPreferences.removeArticleFromBookmark(articleId, articleTitle);
                    Drawable drawable = setTint(ContextCompat.getDrawable(DetailedArticle.this, R.drawable.ic_bookmark_border_black), R.color.colorPrimary);
                    fab.setImageDrawable(drawable);
                }
                else{
                    bookmarkSharedPreferences.addArticleToBookmark(articleId, detailedNews, articleTitle);
                    Drawable drawable = setTint(ContextCompat.getDrawable(DetailedArticle.this, R.drawable.ic_bookmark_black), R.color.colorPrimary);
                    fab.setImageDrawable(drawable);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detailed_page_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.detailed_share);
        Drawable drawable = setTint(ContextCompat.getDrawable(DetailedArticle.this, R.drawable.ic_share_black_24dp), R.color.bookmark);
        shareItem.setIcon(drawable);
        shareItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View customView = inflater.inflate(R.layout.share_dialog_box,null);
                PopupWindow shareWindow = new PopupWindow( customView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                shareWindow.setBackgroundDrawable(new BitmapDrawable());
                shareWindow.setOutsideTouchable(true);

                ImageButton twitterButton = customView.findViewById(R.id.dialog_twitter_button);
                ImageButton facebookButton = customView.findViewById(R.id.dialog_facebook_button);
                ImageButton emailButton = customView.findViewById(R.id.dialog_email_button);

                twitterButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        String twitterUrl = "https://twitter.com/intent/tweet?text=Check%20out%20this%20Link:&url="+newsUrl+"&hashtags=CSCI571NewsSearch";
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl)));
                    }
                });

                facebookButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ShareDialog shareDialog = new ShareDialog(DetailedArticle.this);
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
                });

                emailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","", null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "#CSCI571 NewsApp");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Check out this link : "+ newsUrl);
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });
                shareWindow.showAtLocation(cardView, Gravity.BOTTOM,0,0);
                return false;
            }
        });
        return true;
    }
    private Drawable setTint(Drawable drawable, int color) {
        if(drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 110, 110, true));
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            return drawable;
        }
        return null;
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
