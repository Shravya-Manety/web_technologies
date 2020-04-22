package com.example.newsapp;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFragment extends Fragment {



    private String queryKeyword = "";
    private EditText googleTrendsEditText;

    public TrendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trending, container, false);

        googleTrendsEditText = view.findViewById(R.id.trends_edit_text);
        googleTrendsEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
        googleTrendsEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        googleTrendsEditText.setHint("Coronavirus");

        googleTrendsEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                System.out.println(event.getAction());
                System.out.println("Keycode:"+keyCode);
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)){
                    queryKeyword = googleTrendsEditText.getText().toString();
                    getGoogleTrendsDataFromNode(queryKeyword, view);
                    return true;
                }
                return false;
            }
        });
        System.out.println("query keyword from editText");
        if(queryKeyword.equalsIgnoreCase(""))
            queryKeyword = "Coronavirus";
        getGoogleTrendsDataFromNode(queryKeyword, view);


        return view;
    }

    private void getGoogleTrendsDataFromNode(final String queryKeyword, final View view) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String googleTrendsNodeUrl = "http://localhost:5000/getGoogleTrendsData/"+queryKeyword;
        StringRequest stringRequestGoogleTrends = new StringRequest(Request.Method.GET, googleTrendsNodeUrl,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {
                        System.out.println("googleTrendsResponse" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<Entry> valuesFromTrends = new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++){
                                valuesFromTrends.add(new Entry(i, jsonArray.getInt(i)));
                            }
                            LineDataSet trendsDataSet = new LineDataSet(valuesFromTrends, "Trending Chart for "+queryKeyword);
                            trendsDataSet.setColor(Color.parseColor("#7E7AC3"));
                            trendsDataSet.setDrawHighlightIndicators(false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                trendsDataSet.setCircleColor(getActivity().getColor(R.color.colorAccent));
                                trendsDataSet.setCircleHoleColor(getActivity().getColor(R.color.colorAccent));
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
                            leftAxis.setAxisLineColor(getActivity().getColor(R.color.colorPrimary));

                            googleTrendsChart.setData(data);
                            googleTrendsChart.invalidate();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TrendingFragment",error.toString());
            }
        });

        queue.add(stringRequestGoogleTrends);
    }

}
