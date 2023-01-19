package com.example.getit;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    final String API_KEY = "3d446d5122eec64d64067f4f907b3247";
    final String WEBSITE_URL = "https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid=3d446d5122eec64d64067f4f907b3247";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;


    String locationProvider = LocationManager.GPS_PROVIDER;


    TextView nameOfCity, weatherState, temperature;
    ImageView weatherIcon;

    RelativeLayout cityFinder;

    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        weatherState = findViewById(R.id.weatherCondition);
        temperature = findViewById(R.id.temperature);
        weatherIcon = findViewById(R.id.weatherIcon);
        cityFinder = findViewById(R.id.cityFinder);
        nameOfCity = findViewById(R.id.cityName);

        cityFinder.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CityFinder.class);
            startActivity(intent);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String city = intent.getStringExtra("City");
        if(city != null)
            getWeatherForNewCity(city);
        else
            getWeatherForCurrentLocation();
    }
    private void getWeatherForNewCity(String city){
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("apiKey" , API_KEY);
        fetchCurrentData(params);
    }

    private void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());


                RequestParams  params = new RequestParams();
                params.put("lat", latitude);
                params.put("long", longitude);
                params.put("apiID", API_KEY);

                fetchCurrentData(params);
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
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(locationProvider, MIN_TIME, MIN_DISTANCE, locationListener);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE){
            if (grantResults.length  > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Location Access granted", Toast.LENGTH_SHORT).show();
            }
            else{

            }
        }
    }
    private void fetchCurrentData(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEBSITE_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(MainActivity.this, "Data Fetched", Toast.LENGTH_SHORT).show();
                WeatherData weatherData = WeatherData.fromJason(response);
                upDateUI(weatherData);
//                super.onSuccess(statusCode, headers, response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    private void upDateUI(WeatherData weatherData){
        temperature.setText(weatherData.getTemperature());
        nameOfCity.setText(weatherData.getCity());
        weatherState.setText(weatherData.getWeatherType());
        int resourceID = getResources().getIdentifier(weatherData.getIcon(), "drwable", getPackageName());
        weatherIcon.setImageResource(resourceID);
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(locationManager != null)
            locationManager.removeUpdates(locationListener);
    }
}