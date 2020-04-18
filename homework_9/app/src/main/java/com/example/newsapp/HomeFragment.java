package com.example.newsapp;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    Double latitude, longitude;
    String cityName , stateName ,summary;
    int temperature;

    ImageView imageViewWeatherCard;
    TextView textViewCityName, textViewStateName, textViewTemp, textViewSummary;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){
            latitude = Double.parseDouble(bundle.getString("latitude"));
            longitude = Double.parseDouble(bundle.getString("longitude"));

            Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cityName = addresses.get(0).getLocality();
            stateName = addresses.get(0).getAdminArea();

//            System.out.println("City: " + cityName);
//            System.out.println("State: " + stateName);
        }



        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String openWeatherUrl = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&units=metric&appid=eed15b03c76139e28686f8836c08e388";
        StringRequest stringRequestOpenWeather = new StringRequest(Request.Method.GET, openWeatherUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        parseOpenWeatherResponse(response, view);
//                        System.out.println("Response from open weather api is :" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("HomeFragment", error.toString());
            }
        });
        queue.add(stringRequestOpenWeather);

        NewsCardFragment newsCardFragment = new NewsCardFragment(getActivity().getApplicationContext());
        Bundle bundleNewsFragment = new Bundle();
        bundleNewsFragment.putString("newsUrl", "http://localhost:5000/guardianHome");
        newsCardFragment.setArguments(bundleNewsFragment);
        MainActivity.fragmentManager.beginTransaction().add(R.id.newscard_fragment_container, newsCardFragment, null).commit();
        return view;
    }

    private void parseOpenWeatherResponse(String response, View view) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray weather = jsonObject.getJSONArray("weather");
            JSONObject weather0 =  weather.getJSONObject(0);
            summary = weather0.getString("main");
            JSONObject main = jsonObject.getJSONObject("main");
            temperature = (int) Math.round(main.getDouble("temp"));
            textViewCityName = view.findViewById(R.id.city_name);
            textViewCityName.setText(cityName);
            textViewStateName = view.findViewById(R.id.state_name);
            textViewStateName.setText(stateName);
            textViewTemp = view.findViewById(R.id.temperature);
//            System.out.println("Temperature :"+temperature);
            textViewTemp.setText(String.valueOf(temperature));
            textViewSummary = view.findViewById(R.id.weather_summary);
//            System.out.println("Summary :"+summary);
            textViewSummary.setText(summary);

            imageViewWeatherCard = view.findViewById(R.id.weather_image);
            String url;
            switch(summary){
                case "Clouds":
                    url = "https://csci571.com/hw/hw9/images/android/cloudy_weather.jpg";
                    break;
                case "Clear":
                    url = "https://csci571.com/hw/hw9/images/android/clear_weather.jpg";
                    break;
                case "Snow":
                    url = "https://csci571.com/hw/hw9/images/android/snowy_weather.jpeg";
                    break;
                case "Rain":
                    url = "https://csci571.com/hw/hw9/images/android/rainy_weather.jpg";
                    break;
                case "Drizzle":
                    url = "https://csci571.com/hw/hw9/images/android/rainy_weather.jpg";
                    break;
                case "Thunderstorm" :
                    url = "https://csci571.com/hw/hw9/images/android/thunder_weather.jpg";
                    break;
                default:
                    url = "https://csci571.com/hw/hw9/images/android/sunny_weather.jpg";
                    break;
            }
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(20));
            Glide.with(getActivity().getApplicationContext())
                    .load(url)
                    .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(imageViewWeatherCard);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
