package com.example.newsapp;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.newsapp.Utilities.EXTRA_NEWS_ARTICLE_ID;
import static com.example.newsapp.Utilities.PARENT_CLASS_NAME;


public class NewsCardRecyclerViewAdapter extends RecyclerView.Adapter<NewsCardRecyclerViewAdapter.NewsCardViewHolder> {

    private List<JSONObject> newsCardItems;
    private Context context;
    private String articleId;
    private String articleTitle;
    private BookmarkSharedPreferences bookmarkSharedPreferences;
    private String parentClassName, newsUrl;
    private MainActivity mainActivity = new MainActivity();

    public ClickListener clickListener;

    public NewsCardRecyclerViewAdapter(){

    }

    public NewsCardRecyclerViewAdapter(List<JSONObject> items,String parentClassName, ClickListener clickListener) {
        newsCardItems = items;
        this.parentClassName = parentClassName;
        this.clickListener = clickListener;
    }

    @Override
    public NewsCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_newscard, parent, false);
        this.context = parent.getContext();
        this.bookmarkSharedPreferences = new BookmarkSharedPreferences(context);
        return new NewsCardViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(final NewsCardViewHolder holder, int position) {
        holder.newsItem = newsCardItems.get(position);
        DateTime newsItemDate = null;
        try {
            String elapsed = Utilities.getTimeFromDate(holder.newsItem.getString("date"));
            Glide.with(context)
                    .load(holder.newsItem.getString("image"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.newsCardImage);
            articleId = holder.newsItem.getString("id");
            articleTitle = holder.newsItem.getString("title");
            holder.newsCardTitle.setText(articleTitle);
            holder.newsCardTime.setText(elapsed);
            holder.newsCardSection.setText(holder.newsItem.getString("section"));
            newsUrl = holder.newsItem.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(bookmarkSharedPreferences.containsArticle(articleId)){
            holder.newsCardBookmarkButton.setImageResource(R.drawable.bookmark_black_18dp);
        }
        else{
            holder.newsCardBookmarkButton.setImageResource(R.drawable.bookmark_border_black_18dp);
        }
    }

    @Override
    public int getItemCount() {
        if(newsCardItems != null)
            return newsCardItems.size();
        return 0;
    }

    public void clear() {
        newsCardItems.clear();
    }

    public void addAll(List<JSONObject> list) {
        newsCardItems.addAll(list);
        notifyDataSetChanged();
    }

    public class NewsCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        final View newsCardsListView;
        final ImageView newsCardImage;
        final TextView newsCardTitle;
        final TextView newsCardTime;
        final TextView newsCardSection;
        final ImageButton newsCardBookmarkButton, newsCardShareButton;
        JSONObject newsItem;
        private WeakReference<ClickListener> listenerRef;
        String titleToDisplay = "";
        String twitterLink = "";


        NewsCardViewHolder(View view, ClickListener clickListener) {
            super(view);

            listenerRef = new WeakReference<>(clickListener);
            newsCardsListView = view;
            newsCardImage = (ImageView)view.findViewById(R.id.newscard_image);
            newsCardTitle = (TextView)view.findViewById(R.id.newscard_title);
            newsCardTime = (TextView)view.findViewById(R.id.newscard_time);
            newsCardSection = (TextView) view.findViewById(R.id.newscard_sectionName);
            newsCardBookmarkButton = (ImageButton)view.findViewById(R.id.newscard_bookmark_button);
            newsCardShareButton = (ImageButton)view.findViewById(R.id.newscard_share_button);

            newsCardsListView.setOnClickListener(this);
            newsCardImage.setOnClickListener(this);
            newsCardBookmarkButton.setOnClickListener(this);
            newsCardShareButton.setOnClickListener(this);
            newsCardsListView.setOnLongClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + newsCardTitle.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            JSONObject bookmarkNewsItem = newsCardItems.get(getAdapterPosition());
            try {
                if(v.getId() == newsCardImage.getId()){

                }
                else if (v.getId() == newsCardBookmarkButton.getId()) {

                    if (bookmarkSharedPreferences.containsArticle(bookmarkNewsItem.getString("id"))) {
                        bookmarkSharedPreferences.removeArticleFromBookmark(bookmarkNewsItem.getString("id"), bookmarkNewsItem.getString("title"));
                        newsCardBookmarkButton.setImageResource(R.drawable.ic_bookmark_border_black);
                    } else {
                        bookmarkSharedPreferences.addArticleToBookmark(bookmarkNewsItem.getString("id"), newsItem, bookmarkNewsItem.getString("title"));
                        newsCardBookmarkButton.setImageResource(R.drawable.ic_bookmark_black);
                    }
                    listenerRef.get().onPositionClicked(getAdapterPosition(),false);

                }else if(v.getId() == newsCardShareButton.getId()){


                    System.out.println("Newscard Share button is clicked");
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

                    // Inflate the custom layout/view
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
                            callTwitterIntent(newsUrl);
                            try {
                                listenerRef.get().onPositionClicked(getAdapterPosition(),false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    facebookButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                listenerRef.get().onPositionClicked(getAdapterPosition(),true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    emailButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callEmailIntent(newsUrl);
                            try {
                                listenerRef.get().onPositionClicked(getAdapterPosition(),false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    shareWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                }
                else {
                    Intent detailedArticleIntent = new Intent(context, DetailedArticle.class);
                    detailedArticleIntent.putExtra(EXTRA_NEWS_ARTICLE_ID, bookmarkNewsItem.getString("id"));
                    detailedArticleIntent.putExtra(PARENT_CLASS_NAME, parentClassName);
                    context.startActivity(detailedArticleIntent);
                    listenerRef.get().onPositionClicked(getAdapterPosition(),false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public boolean onLongClick(final View v) {

            final JSONObject newsCardObj = newsCardItems.get(getAdapterPosition());

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.long_press_dialog);

            ImageView newsCardImageView = (ImageView) dialog.findViewById(R.id.alert_dialog_image);
            TextView newsCardTitle = (TextView) dialog.findViewById(R.id.alert_dialog_title);
            final ImageButton alertDialogBookmark = (ImageButton) dialog.findViewById(R.id.alert_dialog_bookmark_button);
            final ImageButton alertDialogTwitter = (ImageButton) dialog.findViewById(R.id.alert_dialog_twitter_button);
            try {
                articleId = newsCardObj.getString("id");
                titleToDisplay = newsCardObj.getString("title");
                twitterLink = newsCardObj.getString("link");
                newsCardTitle.setText(titleToDisplay);
                Glide.with(context)
                        .load(newsCardObj.getString("image"))
                        .into(newsCardImageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(bookmarkSharedPreferences.containsArticle(articleId)){
                alertDialogBookmark.setImageResource(R.drawable.ic_bookmark_black);
            }
            else{
                alertDialogBookmark.setImageResource(R.drawable.ic_bookmark_border_black);
            }
            alertDialogBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(bookmarkSharedPreferences.containsArticle(articleId)){
                        bookmarkSharedPreferences.removeArticleFromBookmark(articleId, titleToDisplay);
                        alertDialogBookmark.setImageResource(R.drawable.ic_bookmark_border_black);
                        ImageButton cardBookmarkButton = v.findViewById(R.id.newscard_bookmark_button);
                        cardBookmarkButton.setImageResource(R.drawable.ic_bookmark_border_black);
                    }
                    else{
                        bookmarkSharedPreferences.addArticleToBookmark(articleId, newsCardObj, titleToDisplay);
                        alertDialogBookmark.setImageResource(R.drawable.ic_bookmark_black);
                        ImageButton cardBookmarkButton = v.findViewById(R.id.newscard_bookmark_button);
                        cardBookmarkButton.setImageResource(R.drawable.ic_bookmark_black);
                    }
                }
            });
            alertDialogTwitter.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    String twitterUrl = "https://twitter.com/intent/tweet?text=Check%20out%20this%20Link:&url="+twitterLink+"&hashtags=CSCI571NewsSearch";
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl)));
                }
            });
            dialog.show();
            listenerRef.get().onLongClicked(getAdapterPosition());
            return true;
        }



        public void callEmailIntent(String newsUrl){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "#CSCI571 NewsApp");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Check out this link : "+ newsUrl);
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }

        public void callTwitterIntent(String newsUrl){
            String twitterUrl = "https://twitter.com/intent/tweet?text=Check%20out%20this%20Link:&url="+newsUrl+"&hashtags=CSCI571NewsSearch";
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl)));
        }


    }
}

