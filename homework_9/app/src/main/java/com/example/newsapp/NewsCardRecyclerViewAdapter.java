package com.example.newsapp;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class NewsCardRecyclerViewAdapter extends RecyclerView.Adapter<NewsCardRecyclerViewAdapter.NewsCardViewHolder> {

    private final List<JSONObject> newsCardItems;
    private Context context;
    private String articleId;
    private String articleTitle;
    private BookmarkSharedPreferences bookmarkSharedPreferences;

    public ClickListener clickListener;

    public NewsCardRecyclerViewAdapter(List<JSONObject> items, Context context, ClickListener clickListener) {
        newsCardItems = items;
        this.context = context;
        this.clickListener = clickListener;
        this.bookmarkSharedPreferences = new BookmarkSharedPreferences(context);
    }

    @Override
    public NewsCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_newscard, parent, false);
        return new NewsCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewsCardViewHolder holder, int position) {
        holder.newsItem = newsCardItems.get(position);

        DateTime newsItemDate = null;
        try {
            newsItemDate = new DateTime(holder.newsItem.getString("date"));
            String elapsed = Utilities.getTimeFromDate(newsItemDate);
            Glide.with(context)
                    .load(holder.newsItem.getString("image"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.newsCardImage);
            articleId = holder.newsItem.getString("id");
            articleTitle = holder.newsItem.getString("title");
            holder.newsCardTitle.setText(articleTitle);
            String timeSectionName = elapsed + " | " + holder.newsItem.getString("section");
            holder.newsCardTimeSectionName.setText(timeSectionName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(bookmarkSharedPreferences.containsArticle(articleId)){
            holder.newsCardBookmarkButton.setImageResource(R.drawable.bookmark_black_18dp);
        }
        else{
            holder.newsCardBookmarkButton.setImageResource(R.drawable.bookmark_border_black_18dp);
        }
        holder.newsCardBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarkSharedPreferences.containsArticle(articleId)){
                    bookmarkSharedPreferences.removeArticleFromBookmark(articleId, articleTitle);
                    holder.newsCardBookmarkButton.setImageResource(R.drawable.bookmark_border_black_18dp);
                }
                else{
                    bookmarkSharedPreferences.addArticleToBookmark(articleId, holder.newsItem, articleTitle);
                    holder.newsCardBookmarkButton.setImageResource(R.drawable.bookmark_black_18dp);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        if(newsCardItems != null)
            return newsCardItems.size();
        return 0;
    }

    public class NewsCardViewHolder extends RecyclerView.ViewHolder {
        final View newsCardsListView;
        final ImageView newsCardImage;
        final TextView newsCardTitle;
        final TextView newsCardTimeSectionName;
        final ImageButton newsCardBookmarkButton;
        JSONObject newsItem;

        NewsCardViewHolder(View view) {
            super(view);
            newsCardsListView = view;
            newsCardImage = (ImageView)view.findViewById(R.id.newscard_image);
            newsCardTitle = (TextView)view.findViewById(R.id.newscard_title);
            newsCardTimeSectionName = (TextView)view.findViewById(R.id.newscard_time_sectionName);
            newsCardBookmarkButton = (ImageButton)view.findViewById(R.id.newscard_bookmark_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + newsCardTitle.getText() + "'";
        }
    }
}

