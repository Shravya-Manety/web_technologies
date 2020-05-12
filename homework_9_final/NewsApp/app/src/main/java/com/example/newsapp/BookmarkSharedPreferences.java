package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookmarkSharedPreferences {

    private SharedPreferences sharedPreferences;
    private Context context;

    public BookmarkSharedPreferences(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);
    }

    public void addArticleToBookmark(String articleId, JSONObject article, String titleToDisplay){
        if(!sharedPreferences.contains(articleId)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(articleId, article.toString());
            editor.commit();
            Toast.makeText(context, "\"" + titleToDisplay + "\"" + " was added to bookmarks", Toast.LENGTH_SHORT).show();
        }
        System.out.println("articles in shared preference after adding " + sharedPreferences.getAll().toString());
    }

    public boolean containsArticle(String articleId){
        if(sharedPreferences.contains(articleId))
            return true;
        return false;
    }
    public void removeArticleFromBookmark(String articleId, String titleToDisplay){
        System.out.println("from sharedpref before remove:"+sharedPreferences.getAll().size());
        System.out.println("article id :" + articleId);
        System.out.println("Shared Preferences contains this id : "+ sharedPreferences.contains(articleId));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(articleId);
        editor.commit();
        System.out.println("from sharedpref after remove:"+sharedPreferences.getAll().size());
        Toast.makeText(context, "\"" + titleToDisplay + "\"" + " was removed from bookmarks", Toast.LENGTH_SHORT).show();
    }
    public List<JSONObject> getAllArticlesFromBookmark(){

        Map<String, String> articles = (HashMap<String, String>)sharedPreferences.getAll();
        System.out.println("articles in sharedpreference " +articles);
        List<JSONObject> bookmarkedArticles = new ArrayList<>();
        for(Map.Entry<String, String> entry : articles.entrySet()){
            try {
                bookmarkedArticles.add(new JSONObject(entry.getValue()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return bookmarkedArticles;
    }

}
