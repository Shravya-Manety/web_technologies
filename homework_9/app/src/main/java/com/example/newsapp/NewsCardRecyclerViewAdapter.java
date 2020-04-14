package com.example.newsapp;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsapp.NewsCardFragment.OnListFragmentInteractionListener;
import com.example.newsapp.dummy.DummyContent.DummyItem;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class NewsCardRecyclerViewAdapter extends RecyclerView.Adapter<NewsCardRecyclerViewAdapter.NewsCardViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public NewsCardRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public NewsCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_newscard, parent, false);
        return new NewsCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewsCardViewHolder holder, int position) {
//        TODO : set values to the cardview from here
        holder.Item = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.newsCardsListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class NewsCardViewHolder extends RecyclerView.ViewHolder {
        public final View newsCardsListView;
        public final ImageView newsCardImage;
        public final TextView newsCardTitle;
        public final TextView newsCardTimeSectionName;
        public JSONObject newsItem;

        public NewsCardViewHolder(View view) {
            super(view);
            newsCardsListView = view;
            newsCardImage = (ImageView)view.findViewById(R.id.newscard_image);
            newsCardTitle = (TextView)view.findViewById(R.id.newscard_title);
            newsCardTimeSectionName = (TextView)view.findViewById(R.id.newscard_time_sectionName);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + newsCardTitle.getText() + "'";
        }
    }
}
