package com.example.newsapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
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
public class HomeFragment extends Fragment implements LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locationManager;
    String provider;
    Double latitude = null,longitude = null;
    String cityName , stateName ,summary;
    int temperature;
    private SwipeRefreshLayout swipeToRefresh;
    private NewsCardFragment newsCardFragment;

    ImageView imageViewWeatherCard;
    TextView textViewCityName, textViewStateName, textViewTemp, textViewSummary;
    View view;


    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        System.out.println("Provider : "+ provider);

        checkLocationPermission();
        getNewsCard();
        swipeToRefresh = view.findViewById(R.id.news_swipeRefresh_home);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsCardFragment.resetAdapterOnRefresh();
                final Handler animationHandler = new Handler();
                animationHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeToRefresh.isRefreshing()) {
                            swipeToRefresh.setRefreshing(false);
                        }
                    }
                }, 1500);
            }
        });
        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.i("Location info: Lat", latitude.toString());
        Log.i("Location info: Lng", longitude.toString());

        generateWeatherCard();
    }

    private void generateWeatherCard() {
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cityName = addresses.get(0).getLocality();
        stateName = addresses.get(0).getAdminArea();

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String openWeatherUrl = "https://api.openweathermap.org/data/2.5/weather?q="+cityName+"&units=metric&appid=eed15b03c76139e28686f8836c08e388";
        StringRequest stringRequestOpenWeather = new StringRequest(Request.Method.GET, openWeatherUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        parseOpenWeatherResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("HomeFragment", error.toString());
            }
        });
        queue.add(stringRequestOpenWeather);

    }

    private void parseOpenWeatherResponse(String response) {
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

    private void getNewsCard() {
        newsCardFragment = new NewsCardFragment(getActivity().getApplicationContext());
        Bundle bundleNewsFragment = new Bundle();
        bundleNewsFragment.putString("newsUrl", "http://localhost:5000/guardianHome");
        bundleNewsFragment.putString("parent", "MainActivity");
        newsCardFragment.setArguments(bundleNewsFragment);
        MainActivity.fragmentManager.beginTransaction().add(R.id.newscard_fragment_container, newsCardFragment, null).commit();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean checkLocationPermission() {
        System.out.println("Inside Check Location Permission");
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("If permission is not granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                System.out.println("Showing an explanation");

                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions( getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                System.out.println("Directly requesting permission");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            System.out.println("Required permissions already exist");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        provider = locationManager.getBestProvider(new Criteria(), false);
        System.out.println("onRequestPermissionResult Request Code :" + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                }
                return;
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Provider value onResume :" + provider);
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationManager.removeUpdates(this);
        }
    }
}
