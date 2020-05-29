package be.kuleuven.softdev.august.leuvbike;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Chronometer;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import android.location.LocationManager;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.view.View.Z;

public class RentalActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView mTextMessage;
    private String url;
    private String urlunlock;
    private String urllock;
    private RequestQueue queue;
    private long base;
    Chronometer simpleChronometer;
    long elapsedtime;
    private Location location;
    public LocationManager locationManager;
    private int chronostate;
    private long mLastStopTime;
    private double currentprice;
    private Criteria criteria;
    private GoogleMap mMap;
    double latint;
    double longint;
    double latend;
    double longend;
    Thread t;



    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(chronostate ==1){
                        long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
                        simpleChronometer.setBase( simpleChronometer.getBase() + intervalOnPause );
                        Log.d("TAG", "loop 2___ " + String.valueOf(intervalOnPause));
                        simpleChronometer.start();
                        chronostate=0;
                        RentalPaused.status =0;
                    }
                    elapsedtime = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
                    updateTextView();
                    Log.d("TAG", String.valueOf(elapsedtime));
                    return true;
                case R.id.navigation_dashboard:
                    simpleChronometer.stop();
                    Intent i = new Intent(RentalActivity.this, RentalPaused.class);
                    elapsedtime = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
                    updateLocation();
                    i.putExtra("TIME", elapsedtime);
                    i.putExtra("LAT",location.getLatitude());
                    i.putExtra("LNG",location.getLongitude());
                    currentprice = ((elapsedtime/18000)*0.01);
                    i.putExtra("COST", currentprice);
                    i.putExtra("Latint", latint);
                    i.putExtra("Longint", longint);
                    i.putExtra("URL", url);
                    startActivity(i);
                    finish();
                    return true;
                case R.id.navigation_notifications:
                    chronostate =1;
                    mTextMessage.setText(R.string.title_notifications);
                    simpleChronometer.stop();
                    mLastStopTime = SystemClock.elapsedRealtime();
                    onBackPressed();
                    // intent pause action

                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Bundle extras = getIntent().getExtras();
        if (savedInstanceState == null) {
            if(extras == null) {
               url= null;
            } else {
                url = extras.getString("URL");

            }
        } else {
            url = (String) savedInstanceState.getSerializable("URL");
        }
        //Declare URLS
        urlunlock ="http://"+url+"/L";
        urllock = "http://"+url+"/H";

        // SEND REQUEST UNLOCK
        // Request a string response from the provided URL.
        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlunlock,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("TAG", "Connected!!!!");
                        Log.d("TAG", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "ERROR CONNECTING");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        simpleChronometer =  findViewById(R.id.simpleChronometer); // initiate a chronometer
        if(RentalPaused.status == 1 ){
            Bundle extra = getIntent().getExtras();
            elapsedtime = extra.getLong("TIME");
            latint = extra.getDouble("Latint");
            longint = extra.getDouble("Longint");
            Log.d("TAG", "loop 1__ " + String.valueOf(elapsedtime));
            simpleChronometer.setBase(SystemClock.elapsedRealtime() - elapsedtime);
            simpleChronometer.start();
            RentalPaused.status =0;
            Log.d("STATUS", String.valueOf(RentalPaused.status));
            chronostate =0;
            updateTextView();
        }else{
            simpleChronometer.setBase(SystemClock.elapsedRealtime());
            simpleChronometer.start();
            updateTextView();
            latint =  1;
            longint = 1;
            latend = 2;
            longend = 2;
            updateLocation(1);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startTimerThread();
    }

    private void startTimerThread() {
        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    private void updateTextView() {
        elapsedtime = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
        currentprice = ((elapsedtime/18000)*0.01);
        mTextMessage.setText("Rental in progress " + "\nCurrent price is " + currentprice + " EUR");
        mTextMessage.invalidate();
        mTextMessage.requestLayout();
        Log.d("TEXTUPDATE", "!!!!!!!!! TEXT UPDATE !!!!!!!!");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(RentalActivity.this);
        builder1.setMessage("Are you sure you want to end your rental?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        endRental();
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void endRental(){
        updateLocation(2);

        Intent i = new Intent(RentalActivity.this, RentalEnd.class);
        //PUT EXTRA's
        elapsedtime = SystemClock.elapsedRealtime() - simpleChronometer.getBase();
        currentprice = ((elapsedtime/5000)*0.01);

        t.interrupt();

        i.putExtra("COST", currentprice);
        i.putExtra("TIME", elapsedtime);
        i.putExtra("Latint", latint);
        i.putExtra("Longint", longint);
        i.putExtra("Latend", latend);
        i.putExtra("Longend", longend);
        i.putExtra("URL", url);


        startActivity(i);
        finish();

    }

    public void updateLocation(int save) {

        enableMyLocation();

        //Auto ga naar huidige locatie
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG", "LOCATION GRANTED!!!");
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

            if(save == 1){ latint = 50.8748769;
                longint = 4.70777529999998;
            }
            if(save == 2){latend = 50.8748769;
                longend = 4.70777529999998;
            }
        }else {Log.d("TAG", "no location recieved");}

    }

    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        //LatLng sydney = new LatLng(-33.852, 151.211);
        //googleMap.addMarker(new MarkerOptions().position(sydney)
        //        .title("Marker in Sydney"));
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        ////

        //mMap.setOnMyLocationButtonClickListener(this);
        //mMap.setOnMyLocationClickListener(this);

        this.mMap=googleMap;
        enableMyLocation();

        updateLocation(0);

        //Log.d("LEAP", "onMapReady: " + location.getLatitude());

        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(18)                   // Sets the zoom
                    //.bearing(90)                // Sets the orientation of the camera to east
                    //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else{
            LatLng gt = new LatLng(50.8748769, 4.70777529999998);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(gt));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        }

        //hide directions-knoppen
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

}
