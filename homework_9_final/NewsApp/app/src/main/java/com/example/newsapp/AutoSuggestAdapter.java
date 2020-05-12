package com.example.newsapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoSuggestAdapter extends ArrayAdapter<String> implements Filterable {

    List<String> autocompleteSuggesstionsList;

    public AutoSuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        autocompleteSuggesstionsList = new ArrayList<String>();
    }

    public void setData(List<String> list) {
        autocompleteSuggesstionsList.clear();
        autocompleteSuggesstionsList.addAll(list);
    }

    @Override
    public int getCount() {
        return autocompleteSuggesstionsList.size();
    }
    @Nullable
    @Override
    public String getItem(int position) {
        return autocompleteSuggesstionsList.get(position);
    }

    public String getObject(int position) {
        return autocompleteSuggesstionsList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = autocompleteSuggesstionsList;
                    filterResults.count = autocompleteSuggesstionsList.size();
                }
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }


}
