package com.bellychallenge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final static String consumerKey = "z1IkzmNsjRoHpJdIXYKYog";
    private final static String consumerSecret = "rop6_novWWNuc7lBflX0868huuc";
    private final static String token = "B7LmhZcIRUQnud1yJZPJ3bYDt7Ncdou8";
    private final static String tokenSecret = "t7ZBtPBU_uYI4Ulks-VoWSrXG0A";
    private final static int PERMISSIONS_RESULT_CODE = 0;
    public final static String EXTRA_LIST = "com.bellychallenge.searchlist";

    public final static String TAG = "TAG";

    private GoogleApiClient mGoogleApiClient;
    private YelpAPI yelpAPI;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        setAPIs();
    }

    public void checkPermissions() {
        boolean permissionGranted = (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED);
        Log.i(TAG, Boolean.toString(permissionGranted));
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                    PERMISSIONS_RESULT_CODE);
        }

    }

    protected synchronized void setAPIs() {
        Log.i(TAG, "setting APIs");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(LocationServices.API)
                .build();
        YelpAPIFactory apiFactory = new YelpAPIFactory(consumerKey, consumerSecret, token, tokenSecret);
        yelpAPI = apiFactory.createAPI();
    }

    public void getRestaurantList(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET },
                    PERMISSIONS_RESULT_CODE);
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            Log.d(TAG, "null");
        }
        else {
            Log.d(TAG, "Location: " + mLastLocation.toString());
            callYelp();
        }
    }

    private void callYelp() {
        Map<String, String> params = new HashMap<>();
        params.put("term", "yelp");
        params.put("sort", "1");
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(mLastLocation.getLatitude())
                .longitude(mLastLocation.getLongitude()).build();
        Call<SearchResponse> call = yelpAPI.search(coordinate, params);
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                passList(searchResponse);
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
            }
        };

        call.enqueue(callback);
    }

    private void passList(SearchResponse searchResponse) {
        ArrayList<Business> businesses = searchResponse.businesses();
        Intent intent = new Intent(this, DisplayList.class);
        ArrayList<BusinessParcelable> bParcelables = new ArrayList<>();
        for (int i = 0; i < businesses.size(); i++) {
            BusinessParcelable businessParcelable = new BusinessParcelable(businesses.get(i));
            bParcelables.add(businessParcelable);
        }
        intent.putParcelableArrayListExtra(EXTRA_LIST, bParcelables);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int resultCode, String permissions[], int[] grantResults) {
        switch(resultCode) {
            case PERMISSIONS_RESULT_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ACCESS LOCATION GRANTED
                    Log.i(TAG, "Permission granted");
                }
                else {
                    Log.i(TAG, "Permission not granted");
                }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // FAILED
    }
}
