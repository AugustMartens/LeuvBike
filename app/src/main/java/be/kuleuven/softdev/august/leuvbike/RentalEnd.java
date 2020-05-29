package be.kuleuven.softdev.august.leuvbike;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RentalEnd extends AppCompatActivity implements OnMapReadyCallback {

    private TextView t;
    long elapsedtime;
    private double currentprice;
    double latint;
    double longint;
    double latend;
    double longend;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseUserRef;
    private DatabaseReference databaseBikeRideRef;
    private DatabaseReference databaseRentalBikeRef;
    private String userId;

    private double priceInDatabase;
    private String tempUser;
    private String tempBike;
    public static final double costPerS = 0.00055555;
    private int bikeId;
    private double oldLat;
    private double oldLng;
    private double currentLat;
    private double currentLng;
    private int amountOfBikeRides = 0;
    private DatabaseReference paymentRef;

    private String url;
    private String urlunlock;
    private String urllock;
    private RequestQueue queue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_end);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        t = findViewById(R.id.tekst);
        setSupportActionBar(toolbar);

        //Krijg waardes uit Rentalactivity
        Bundle extra = getIntent().getExtras();
        elapsedtime = extra.getLong("TIME");
        currentprice = extra.getDouble ("COST");
        oldLat = extra.getDouble("Latint");
        oldLng = extra.getDouble("Longint");
        currentLat = extra.getDouble("Latend");
        currentLng = extra.getDouble("Longend");
        url = extra.getString("URL");

        latint = oldLat;
        longint = oldLng;
        latend = currentLat;
        longend = currentLng;

        setTekst();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseUserRef = database.getReference("Users");
        databaseBikeRideRef = database.getReference("BikeRides");
        databaseRentalBikeRef = database.getReference("RentalBike");
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();
        bikeId = 1;

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user signed in
                } else {
                    //user signed out
                }
            }
        };

        //Declare URLS
        urlunlock ="http://"+url+"/L";
        urllock = "http://"+url+"/H";

        // Lock
        // SEND REQUEST UNLOCK
        // Request a string response from the provided URL.
        queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urllock,
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

    }

    void setTekst(){
        t.setText("Rental Receipt: \n----\nDuration: " + elapsedtime + "\nPrice: " + currentprice + " EURO \nLatitude Begin: " + latint + "\nLongitude Begin: " + longint + "\nLatitude End: " + latend + "\nLongitude End: " + longend );

    }

    public void onMapReady(GoogleMap mMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng begin = new LatLng(latint, longint);
        LatLng end = new LatLng(latend, longend);

        mMap.addMarker(new MarkerOptions().position(begin)
                .title("Start"));

        mMap.addMarker(new MarkerOptions().position(end)
                .title("End"));

        ArrayList<LatLng> points = new ArrayList<LatLng>();
        PolylineOptions polyLineOptions = new PolylineOptions();
        points.add(begin);
        points.add(end);
        polyLineOptions.width(8);
        polyLineOptions.geodesic(true);
        polyLineOptions.color(Color.BLUE);
        polyLineOptions.addAll(points);
        Polyline polyline = mMap.addPolyline(polyLineOptions);
        polyline.setGeodesic(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(end)      // Sets the center of the map to location user
                .zoom(18)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void onBackPressed(){

        //huidige AmountToPay ophalen & vervolgens updaten.

        Query query1 = databaseUserRef.orderByChild("username").equalTo(userId);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        priceInDatabase = data.child("amountToPay").getValue(double.class);
                        Log.d("TAG","We zijn er in geraakt in de amounttopay"+ priceInDatabase);
                        tempUser = data.getKey();
                        databaseUserRef.child(tempUser).child("amountToPay").setValue(priceInDatabase+currentprice);
                    }
                }
                else{
                    Log.d("TAG","Niet er in geraakt in amounttopay");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /* databaseUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("username").getValue(String.class).equals(userId)){
                        priceInDatabase = ds.child("amountToPay").getValue(double.class);
                        tempUser = ds.getKey();



                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

        //user updaten



        //nieuwe BikeRide toevoegen
        //oude Lat en Lng ophalen uit database
        Query query = databaseRentalBikeRef.orderByChild("bikeId").equalTo(bikeId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d("TAG","We zijn er in geraaakt in de bikecoordinaten");
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        oldLat = data.child("latitude").getValue(double.class);
                        oldLng = data.child("longitude").getValue(double.class);
                        Log.d("TAG","coo zijn "+oldLat+" "+oldLng);

                    }
                }
                else{
                    Log.d("TAG","Niet er in geraakt in bikecoordinaten");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //#bestaande bikerides tellen
        databaseBikeRideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TAG","amoumt: " + amountOfBikeRides);
                amountOfBikeRides = (int) dataSnapshot.getChildrenCount();
                Log.d("TAG","amoumt: " + amountOfBikeRides);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        int i = (int)(elapsedtime/1000);

        Log.d("TAG","coo zijn nu: "+oldLat+"  "+oldLng);
        amountOfBikeRides++;

        saveNewBikeRideExistingBike(oldLat,oldLng,currentLat,currentLng,bikeId, i,amountOfBikeRides);


        //Update Location of used Bike

        Query query2 = databaseRentalBikeRef.orderByChild("bikeId").equalTo(bikeId);
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d("TAG","We zijn er in geraaakt in de bikecoordinaten");
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        tempBike = data.getKey();
                        databaseRentalBikeRef.child(tempBike).child("latitude").setValue(currentLat);
                        databaseRentalBikeRef.child(tempBike).child("longitude").setValue(currentLng);
                    }
                }
                else{
                    Log.d("TAG","Niet er in geraakt in bikecoordinaten");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        Toast.makeText(getApplicationContext(),"Saving Bike Ride to Database", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(RentalEnd.this, MainActivity.class);
        startActivity(intent);




    }



    public void saveNewBikeRideExistingBike(double startLat, double startLng, double endLat, double endLng, int bikeId, int rideDuration, int rideId){
        Query query4 = databaseRentalBikeRef.orderByChild("bikeId").equalTo(bikeId);
        query4.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d("TAG","We zijn er in geraaakt");
                    Log.d("TAG","Coo zijn nu: "+ startLat + "   "+startLng);

                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        RentalBike rentalBike = data.getValue(RentalBike.class);
                        saveNewBikeRide(startLat,startLng,endLat,endLng,rentalBike,calculateDistance(endLat-startLat,startLng-endLng),rideDuration,rideId,rideDuration*costPerS);
                    }
                }
                else{
                    Log.d("TAG","Niet er in geraakt");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void saveNewBikeRide(double startLat, double startLng, double endLat, double endLng, RentalBike usedBike, double distanceRidden, int rideDuration, int rideId, double cost) {

        BikeRide bikeRide = new BikeRide(startLat, startLng, endLat, endLng, usedBike, distanceRidden, rideDuration, rideId, userId, cost);
        saveBikeRide(bikeRide, rideId);
    }

    public void saveBikeRide(BikeRide bikeRide, int rideId) {
        databaseBikeRideRef.push().setValue(bikeRide);


       /* FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mUserRef = database.getInstance().getReference("Users");
        tempArray = new ArrayList<>();
        tempUser = "";

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.child("username").getValue()==userId){
                        tempArray = ds.child("bikeRides").getValue(ArrayList.class);
                        tempUser = ds.getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tempArray.add(bikeRide);

        mUserRef.child(tempUser).child("username").push().setValue(tempArray);

*/
    }

    public double calculateDistance(double lat, double lng){
        double tempResult = lng*111.320*Math.cos(0.0174532925*lat);
        double tempResult2 = tempResult*tempResult;
        double tempResult3 = lat*110.574*lat*110.574;
        double result = Math.sqrt(tempResult2+tempResult3);

        return result;
    }
}
