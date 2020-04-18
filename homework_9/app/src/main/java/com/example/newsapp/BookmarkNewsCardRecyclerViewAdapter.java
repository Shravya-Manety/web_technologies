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

public class BookmarkNewsCardRecyclerViewAdapter extends RecyclerView.Adapter<BookmarkNewsCardRecyclerViewAdapter.BookmarkNewsCardViewHolder> {

    private BookmarkSharedPreferences bookmarkSharedPreferences;
    private List<JSONObject> bookmarkedArticles;
    private Context context;
    private String articleId, articleTitle;

    public BookmarkNewsCardRecyclerViewAdapter(List<JSONObject> items, Context context) {
        this.context = context;
        bookmarkedArticles = items;
        this.bookmarkSharedPreferences = new BookmarkSharedPreferences(context);
    }

    @Override
    public BookmarkNewsCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bookmarknewscard, parent, false);
        return new BookmarkNewsCardViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(final BookmarkNewsCardViewHolder holder, int position) {
        holder.bookmarkNewsItem = bookmarkedArticles.get(position);
        DateTime newsItemDate = null;
        try {
            newsItemDate = new DateTime(holder.bookmarkNewsItem.getString("date"));
            String elapsed = Utilities.getTimeFromDate(newsItemDate);
            Glide.with(context)
                    .load(holder.bookmarkNewsItem.getString("image"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.bookmarkNewsCardImageView);
            articleId = holder.bookmarkNewsItem.getString("id");
            articleTitle = holder.bookmarkNewsItem.getString("title");
            holder.bookmarkTitleTextView.setText(articleTitle);
            holder.bookmarkTime.setText(elapsed);
            holder.bookmarkSectionName.setText(holder.bookmarkNewsItem.getString("section"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.bookmarkbutton.setImageResource(R.drawable.bookmark_black_18dp);
//        holder.bookmarkbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bookmarkSharedPreferences.removeArticleFromBookmark(articleId, articleTitle);
//                holder.bookmarkbutton.setImageResource(R.drawable.bookmark_border_black_18dp);
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return bookmarkedArticles.size();
    }

    public class BookmarkNewsCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View bookmarkNewsCardView;
        public final ImageView bookmarkNewsCardImageView;
        public final TextView bookmarkTitleTextView;
        public final TextView bookmarkTime;
        public final TextView bookmarkSectionName;
        public final ImageButton bookmarkbutton;
        JSONObject bookmarkNewsItem;
        private BookmarkNewsCardRecyclerViewAdapter bookmarkNewsCardRecyclerViewAdapter;


        public BookmarkNewsCardViewHolder(View view, BookmarkNewsCardRecyclerViewAdapter adapter) {
            super(view);
            bookmarkNewsCardView = view;
            bookmarkNewsCardImageView = view.findViewById(R.id.bookmark_newscard_image);
            bookmarkTitleTextView = (TextView) view.findViewById(R.id.bookmark_newscard_title);
            bookmarkTime = (TextView) view.findViewById(R.id.bookmark_newscard_time);
            bookmarkSectionName = (TextView) view.findViewById(R.id.bookmark_newscard_sectionName);
            bookmarkbutton = (ImageButton) view.findViewById(R.id.bookmark_newscard_bookmark_button);
            this.bookmarkNewsCardRecyclerViewAdapter = adapter;
            bookmarkbutton.setOnClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + bookmarkTitleTextView.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            System.out.println("in on click before:" + bookmarkedArticles.size());
            int clickPosition = getLayoutPosition();
            JSONObject clickedArticle = bookmarkedArticles.get(clickPosition);
            try {
                bookmarkSharedPreferences.removeArticleFromBookmark(clickedArticle.getString("id"), clickedArticle.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bookmarkedArticles = bookmarkSharedPreferences.getAllArticlesFromBookmark();
            System.out.println("in on click after:" + bookmarkedArticles.size());
            bookmarkNewsCardRecyclerViewAdapter.notifyDataSetChanged();
        }
    }
}
