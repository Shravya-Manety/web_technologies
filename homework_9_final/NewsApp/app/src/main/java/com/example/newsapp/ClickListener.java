package com.example.newsapp;

import org.json.JSONException;

public interface ClickListener {

    void onPositionClicked(int position, boolean isFacebook) throws JSONException;

    void onLongClicked(int position);
}
