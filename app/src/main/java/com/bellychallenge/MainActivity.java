package com.bellychallenge;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public final static String EXTRA_STORE = "com.bellychallenge.store";

    public final static String TAG = "LOG_TAG";

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
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                    PERMISSIONS_RESULT_CODE);
        }

    }

    protected synchronized void setAPIs() {
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
            readFromDatabase();
        }
        else {
            Log.d(TAG, "Location: " + mLastLocation.toString());
            callYelp();
        }
    }

    private void callYelp() {
        Map<String, String> params = new HashMap<>();
//        params.put("term", "yelp");
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
                Log.e(TAG, "failure");
                readFromDatabase();
            }
        };

        call.enqueue(callback);
    }

    private void passList(SearchResponse searchResponse) {
        ArrayList<Business> businesses = searchResponse.businesses();
        new DownloadImageTask(this).execute(businesses);
    }

    private void nextActivity(ArrayList<BusinessParcelable> bParcelables, boolean needToStore) {
        Log.d(TAG, "NEXT ACTIVITY: " + Boolean.toString(bParcelables.get(0).getImageArr() == null));
        Intent intent = new Intent(this, DisplayList.class);
        intent.putParcelableArrayListExtra(EXTRA_LIST, bParcelables);
        intent.putExtra(EXTRA_STORE, needToStore);
        startActivity(intent);
    }

    private void readFromDatabase() {
        BusinessDbHelper dbHelper = new BusinessDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + BusinessDbHelper.BusinessEntry.TABLE_NAME, null);
        if (cursor.getCount() > 0)
        {
            ArrayList<BusinessParcelable> bPars = new ArrayList<>();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(BusinessDbHelper.BusinessEntry.COLUMN_NAME_TITLE));
                double distance = cursor.getDouble(cursor.getColumnIndex(BusinessDbHelper.BusinessEntry.COLUMN_NAME_DISTANCE));
                String type = cursor.getString(cursor.getColumnIndex(BusinessDbHelper.BusinessEntry.COLUMN_NAME_TYPE));
                String url = cursor.getString(cursor.getColumnIndex(BusinessDbHelper.BusinessEntry.COLUMN_NAME_URL));
                byte[] imageArr = cursor.getBlob(cursor.getColumnIndex(BusinessDbHelper.BusinessEntry.COLUMN_NAME_IMAGE));
                boolean isClosed = cursor.getInt(cursor.getColumnIndex(BusinessDbHelper.BusinessEntry.COLUMN_NAME_CLOSED)) == 1;
                bPars.add(new BusinessParcelable(name, distance, type, url, imageArr, isClosed));
            }
            nextActivity(bPars, false);

        } else
        {
            // I AM EMPTY
            Toast.makeText(this, getResources().getString(R.string.no_connection_first), Toast.LENGTH_LONG).show();
        }
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

    public class DownloadImageTask extends AsyncTask<ArrayList<Business>, Void, ArrayList<BusinessParcelable>> {
        Context context;
        ProgressDialog loadingDialog;

        public DownloadImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingDialog = new ProgressDialog(context);
            loadingDialog.setMessage(context.getResources().getString(R.string.no_connection_first));
            loadingDialog.show();
        }

        @Override
        protected ArrayList<BusinessParcelable> doInBackground(ArrayList<Business>... bs) {
            ArrayList<Business> businesses = bs[0];
            ArrayList<BusinessParcelable> bParcelables = new ArrayList<>();
            for (int i = 0; i < businesses.size(); i++) {
                Business b = businesses.get(i);
                String imageURL = b.imageUrl();
                Bitmap bMap = null;
                try {
                    InputStream in = new java.net.URL(imageURL).openStream();
                    bMap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BusinessParcelable businessParcelable = new BusinessParcelable(businesses.get(i), bMap);
                bParcelables.add(businessParcelable);
            }
            return bParcelables;
        }

        protected void onPostExecute(ArrayList<BusinessParcelable> result) {
            loadingDialog.dismiss();
            clearDb();
            storeInDb(result);
            nextActivity(result, true);
        }
    }

    private void clearDb() {
        BusinessDbHelper dbHelper = new BusinessDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM "+ BusinessDbHelper.BusinessEntry.TABLE_NAME);
    }

    private void storeInDb(ArrayList<BusinessParcelable> bArr) {
        BusinessDbHelper dbHelper = new BusinessDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < bArr.size(); i++) {
            BusinessParcelable b = bArr.get(i);
            ContentValues values = new ContentValues();
            values.put(BusinessDbHelper.BusinessEntry.COLUMN_NAME_ENTRY_ID, i);
            values.put(BusinessDbHelper.BusinessEntry.COLUMN_NAME_TITLE, b.getName());
            values.put(BusinessDbHelper.BusinessEntry.COLUMN_NAME_DISTANCE, b.getDistance());
            values.put(BusinessDbHelper.BusinessEntry.COLUMN_NAME_TYPE, b.getType());
            values.put(BusinessDbHelper.BusinessEntry.COLUMN_NAME_URL, b.getUrl());
            values.put(BusinessDbHelper.BusinessEntry.COLUMN_NAME_IMAGE, b.getImageArr());
            values.put(BusinessDbHelper.BusinessEntry.COLUMN_NAME_CLOSED, b.getClosed());
            db.insert(
                    BusinessDbHelper.BusinessEntry.TABLE_NAME,
                    null,
                    values);
        }
        db.close();
    }

}
