package com.example.newsapp;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.example.newsapp.Utilities.EXTRA_NEWS_ARTICLE_ID;
import static com.example.newsapp.Utilities.PARENT_CLASS_NAME;

public class BookmarkNewsCardRecyclerViewAdapter extends RecyclerView.Adapter<BookmarkNewsCardRecyclerViewAdapter.BookmarkNewsCardViewHolder> {

    private BookmarkSharedPreferences bookmarkSharedPreferences;
    private List<JSONObject> bookmarkedArticles;
    private Context context;
    private String articleTitle;
    private ClickListener clickListener;

    BookmarkNewsCardRecyclerViewAdapter(List<JSONObject> items, ClickListener clickListener) {
        bookmarkedArticles = items;
        this.clickListener = clickListener;
    }

    @Override
    public BookmarkNewsCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bookmarknewscard, parent, false);
        this.context = parent.getContext();
        this.bookmarkSharedPreferences = new BookmarkSharedPreferences(context);
        return new BookmarkNewsCardViewHolder(view, this, clickListener);
    }

    @Override
    public void onBindViewHolder(final BookmarkNewsCardViewHolder holder, int position) {
        holder.bookmarkNewsItem = bookmarkedArticles.get(position);
        try {
            String formattedDate = Utilities.getArticleDate(holder.bookmarkNewsItem.getString("date"), "bookmarks");
            Glide.with(context)
                    .load(holder.bookmarkNewsItem.getString("image"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.bookmarkNewsCardImageView);
            articleTitle = holder.bookmarkNewsItem.getString("title");
            holder.bookmarkTitleTextView.setText(articleTitle);
            holder.bookmarkTime.setText(formattedDate);
            holder.bookmarkSectionName.setText(holder.bookmarkNewsItem.getString("section"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.bookmarkbutton.setImageResource(R.drawable.bookmark_black_18dp);
    }

    @Override
    public int getItemCount() {
        return bookmarkedArticles.size();
    }

    public class BookmarkNewsCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public final View bookmarkNewsCardView;
        public final ImageView bookmarkNewsCardImageView;
        public final TextView bookmarkTitleTextView;
        public final TextView bookmarkTime;
        public final TextView bookmarkSectionName;
        public final ImageButton bookmarkbutton;
        JSONObject bookmarkNewsItem;
        private BookmarkNewsCardRecyclerViewAdapter bookmarkNewsCardRecyclerViewAdapter;
        private WeakReference<ClickListener> clickListenerWeakRef;
        String articleId, titleToDisplay = "", twitterLink = "";


        BookmarkNewsCardViewHolder(View view, BookmarkNewsCardRecyclerViewAdapter adapter, ClickListener clickListener) {
            super(view);

            clickListenerWeakRef = new WeakReference<>(clickListener);
            bookmarkNewsCardView = view;
            bookmarkNewsCardImageView = view.findViewById(R.id.bookmark_newscard_image);
            bookmarkTitleTextView = view.findViewById(R.id.bookmark_newscard_title);
            bookmarkTime = view.findViewById(R.id.bookmark_newscard_time);
            bookmarkSectionName = view.findViewById(R.id.bookmark_newscard_sectionName);
            bookmarkbutton = view.findViewById(R.id.bookmark_newscard_bookmark_button);
            this.bookmarkNewsCardRecyclerViewAdapter = adapter;

            bookmarkNewsCardView.setOnClickListener(this);
            bookmarkNewsCardImageView.setOnClickListener(this);
            bookmarkbutton.setOnClickListener(this);
            bookmarkNewsCardView.setOnLongClickListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + bookmarkTitleTextView.getText() + "'";
        }

        private void removeArticleFromBookmarkSharedPref(JSONObject clickedArticle, boolean isLongClick) throws JSONException {
            bookmarkSharedPreferences.removeArticleFromBookmark(clickedArticle.getString("id"), clickedArticle.getString("title"));
            bookmarkedArticles = bookmarkSharedPreferences.getAllArticlesFromBookmark();
            bookmarkNewsCardRecyclerViewAdapter.notifyDataSetChanged();
            if(isLongClick) {
                clickListenerWeakRef.get().onLongClicked(getAdapterPosition());
            }
        }

        @Override
        public void onClick(View v) {

            JSONObject clickedNewsCardObj = bookmarkedArticles.get(getAdapterPosition());
            try {
                if(v.getId() == bookmarkNewsCardImageView.getId()){
                }
                else if(v.getId() == bookmarkbutton.getId()){
                    removeArticleFromBookmarkSharedPref(clickedNewsCardObj, false);
                }
                else{
                    Intent detailedArticleIntent = new Intent(context, DetailedArticle.class);
                    detailedArticleIntent.putExtra(EXTRA_NEWS_ARTICLE_ID, clickedNewsCardObj.getString("id"));
                    detailedArticleIntent.putExtra(PARENT_CLASS_NAME, "MainActivity");
                    context.startActivity(detailedArticleIntent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                clickListenerWeakRef.get().onPositionClicked(getAdapterPosition(), false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if(v.getId() == bookmarkNewsCardImageView.getId()){
            }
            else{

                final JSONObject clickedNewsCardObj = bookmarkedArticles.get(getAdapterPosition());

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.long_press_dialog);

                ImageView alertDialogImageView = dialog.findViewById(R.id.alert_dialog_image);
                TextView alertDialogTitle = dialog.findViewById(R.id.alert_dialog_title);
                final ImageButton alertDialogBookmark = dialog.findViewById(R.id.alert_dialog_bookmark_button);
                final ImageButton alertDialogTwitter = dialog.findViewById(R.id.alert_dialog_twitter_button);
                try {
                    articleId = clickedNewsCardObj.getString("id");
                    titleToDisplay = clickedNewsCardObj.getString("title");
                    twitterLink = clickedNewsCardObj.getString("link");
                    alertDialogTitle.setText(titleToDisplay);
                    Glide.with(context)
                            .load(clickedNewsCardObj.getString("image"))
                            .into(alertDialogImageView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                alertDialogBookmark.setImageResource(R.drawable.ic_bookmark_black);
                alertDialogBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            removeArticleFromBookmarkSharedPref(clickedNewsCardObj, true);
                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
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

                return true;
            }
            return false;
        }
    }

    void resetBookmarks(List<JSONObject> bookmarksList){
        bookmarkedArticles.clear();
        bookmarkedArticles.addAll(bookmarksList);
        notifyDataSetChanged();
    }
}
