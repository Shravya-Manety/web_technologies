package com.example.newsapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.example.newsapp.Utilities.BACKEND_URL;

public class TrendingFragment extends Fragment {

    private String queryKeyword = "";
    private EditText googleTrendsEditText;

    public TrendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_trending, container, false);

        googleTrendsEditText = view.findViewById(R.id.trends_edit_text);
        googleTrendsEditText.setHint("Coronavirus");

        googleTrendsEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println(event.getAction());
                System.out.println("Keycode:"+keyCode);
                if ((event.getAction() == KeyEvent.ACTION_DOWN || keyCode == KeyEvent.KEYCODE_ENTER)){
                    queryKeyword = googleTrendsEditText.getText().toString();
                    getGoogleTrendsDataFromNode(queryKeyword, view);
                    return true;
                }
                return false;
            }
        });
        if(queryKeyword.equalsIgnoreCase(""))
            queryKeyword = "Coronavirus";
        getGoogleTrendsDataFromNode(queryKeyword, view);
        return view;
    }

    private void getGoogleTrendsDataFromNode(final String queryKeyword, final View view) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String googleTrendsNodeUrl = BACKEND_URL + "getGoogleTrendsData/"+queryKeyword;
        JsonArrayRequest jsonArrayRequestGoogleTrends = new JsonArrayRequest(Request.Method.GET, googleTrendsNodeUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Entry> valuesFromTrends = new ArrayList<>();
                for(int i=0; i<response.length(); i++){
                    try {
                        valuesFromTrends.add(new Entry(i, response.getInt(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                LineDataSet trendsDataSet = new LineDataSet(valuesFromTrends, "Trending Chart for "+queryKeyword);
                trendsDataSet.setColor(Color.parseColor("#7E7AC3"));
                trendsDataSet.setDrawHighlightIndicators(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    trendsDataSet.setCircleColor(getActivity().getColor(R.color.colorPrimaryDark));
                    trendsDataSet.setCircleHoleColor(getActivity().getColor(R.color.colorPrimaryDark));
                }

                LineData data = new LineData(trendsDataSet);
                LineChart googleTrendsChart = view.findViewById(R.id.trends_line_chart);

                Legend l = googleTrendsChart.getLegend();
                l.setEnabled(true);
                l.setTextSize(17);
                l.setFormSize(17);
                l.setWordWrapEnabled(true);
                l.setMaxSizePercent(0.90f);

                googleTrendsChart.getAxisLeft().setDrawGridLines(false);
                googleTrendsChart.getXAxis().setDrawGridLines(false);
                googleTrendsChart.getAxisRight().setDrawGridLines(false);

                YAxis leftAxis = googleTrendsChart.getAxisLeft();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    leftAxis.setAxisLineColor(getActivity().getColor(R.color.colorWhite));
                }
                googleTrendsChart.setData(data);
                googleTrendsChart.invalidate();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TrendingFragment",error.toString());
            }
        });
        queue.add(jsonArrayRequestGoogleTrends);
    }

}
