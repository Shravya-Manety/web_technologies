package com.example.newsapp;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BookmarksFragment extends Fragment {


    BookmarkNewsCardFragment bookmarkNewsCardFragment;
    public BookmarksFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bookmarkNewsCardFragment = new BookmarkNewsCardFragment(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        MainActivity.fragmentManager.beginTransaction().add(R.id.bookmarks_fragment_container, bookmarkNewsCardFragment).addToBackStack("bookmarks fragment").commit();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bookmarkNewsCardFragment.resetAdapter();
    }
}
