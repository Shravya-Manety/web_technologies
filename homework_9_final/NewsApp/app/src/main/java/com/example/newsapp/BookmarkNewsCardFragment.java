package com.example.newsapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;
import java.util.List;

public class BookmarkNewsCardFragment extends Fragment {

    private BookmarkSharedPreferences bookmarkSharedPreferences;
    private Context context;
    private List<JSONObject> bookmarkedArticles;
    private BookmarkNewsCardRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView no_bookmarks_text;

    public BookmarkNewsCardFragment(Context context) {
        this.context = context;
        this.bookmarkSharedPreferences = new BookmarkSharedPreferences(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarknewscard_list, container, false);
        no_bookmarks_text = view.findViewById(R.id.no_bookmarks);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.bookmark_list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        bookmarkedArticles = bookmarkSharedPreferences.getAllArticlesFromBookmark();
        adapter = new BookmarkNewsCardRecyclerViewAdapter(bookmarkedArticles, new ClickListener() {
            @Override
            public void onPositionClicked(int position, boolean isFacebook) {
                Log.d("BookmarkCardFragment", "Click event occured");
                displayNoArticles();
            }
            @Override
            public void onLongClicked(int position) {
                Log.d("BookmarkCardFragment", "Long Click event occured");
                displayNoArticles();
            }
            private void displayNoArticles() {
                if(bookmarkSharedPreferences.getAllArticlesFromBookmark().size() == 0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    no_bookmarks_text.setVisibility(View.VISIBLE);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        toDisplayBookmarks();
        return view;
    }

    private void toDisplayBookmarks() {
        if(((BookmarkNewsCardRecyclerViewAdapter)recyclerView.getAdapter()).getItemCount() == 0){
            recyclerView.setVisibility(View.INVISIBLE);
            no_bookmarks_text.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            no_bookmarks_text.setVisibility(View.INVISIBLE);
        }
    }
    public void resetAdapter(){
        if(recyclerView != null){
            System.out.println("resetAdapter in bookmark fragment is called");
            bookmarkedArticles = bookmarkSharedPreferences.getAllArticlesFromBookmark();
            adapter = (BookmarkNewsCardRecyclerViewAdapter) recyclerView.getAdapter();
            adapter.resetBookmarks(bookmarkedArticles);
            toDisplayBookmarks();
        }
    }
}
