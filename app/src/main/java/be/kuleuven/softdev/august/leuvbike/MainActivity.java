package be.kuleuven.softdev.august.leuvbike;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import be.kuleuven.softdev.august.leuvbike.dummy.DummyContent;
//import okhttp3.internal.cache.DiskLruCache;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,OnMyLocationButtonClickListener,
        OnMyLocationClickListener,ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private Location location;

    private FirebaseAuth mAuth;
    // private FirebaseUser currentUser;
    public LocationManager locationManager;
    private String phoneNumber;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String userId;
    private FirebaseDatabase firebaseDatabase;

    private ArrayList<BikeRide> tempArray = new ArrayList<BikeRide>();
    private String tempUser = "";
    boolean userFound = false;
    public static final double costPerS = 0.00055555;




    ChildEventListener mChildEventListener;
    DatabaseReference databaseRentalBikeRef = FirebaseDatabase.getInstance().getReference("RentalBike");
    DatabaseReference databaseBikeRideRef = FirebaseDatabase.getInstance().getReference("BikeRides");
    DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users");




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button button = findViewById(R.id.button3);
        button.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        setSupportActionBar(toolbar);




        // FIREBASE
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();

        //zet telefoonnummer in bovenste menu
        /*TextView t = (TextView)findViewById(R.id.phonenumber);
        t.setText(phoneNumber);*/

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if(data.child("username").getValue(String.class).equals(userId)){
                        //code te doen als user al bestaat
                        userFound = true;

                    }
                }
                if(!userFound){
                    //nieuwe user creeeren.

                    //phonenumber pullen vanuit vorige activity
                    Bundle extras = getIntent().getExtras();
                    if(extras != null){
                        phoneNumber = extras.getString("phoneNumber");

                    }
                    User user = new User(userId,0.00, phoneNumber);

                    //user opslaan naar database
                    mUserRef.push().setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //t.setText(currentUser.getPhoneNumber());

        //gooi user naar login indien niet ingelogd (stopt werken van de skip knop)
        /*
        if(currentUser == null){
            Intent ld = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(ld);}
        */

        /**FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
        }
        });
         */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        //TextView phoneNmb = (TextView) headerView.findViewById(R.id.phonenumber);
        //phoneNmb.setText(phoneNumber);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener(){
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem){
                        int id = menuItem.getItemId();
                        menuItem.setChecked(true);
                        drawer.closeDrawers();

                        if(id==R.id.bikerides_overview){
                            Intent intent = new Intent(MainActivity.this, BikeRideViewActivity.class);
                            startActivity(intent);
                        }

                        else if(id == R.id.payment){
                            Intent intent2 = new Intent(MainActivity.this,PaymentViewActivity.class );
                            startActivity(intent2);

                        }

                        return true;
                    };
                });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference("Profile");


        //saving a marker into the database.
        /**DatabaseReference mProfilRef = FirebaseDatabase.getInstance().getReference("Profile");
         FirebaseMarker marker = new FirebaseMarker(50.875448,4.7082313, 1);
         mProfilRef.push().setValue(marker);
         */

        //maak enkele testfietsen aan
        /*RentalBike fiets1 = new RentalBike(50.8,4.71,1,"Stijn",true);
        RentalBike fiets2 = new RentalBike(50.83, 4.716, 2, "August",false);
*/
        /*addRentalBike(fiets1);
        addRentalBike(fiets2);*/
        /*addNewRentalBike(50.8748697,4.7079464,1,"August",true);
        addNewRentalBike(50.875,4.707,2,"Stijn", false);
        addNewRentalBike(50.876,4.705,3,"Pierre", false);
        addNewRentalBike(50.879,4.708,4,"Albert", false);
        addNewRentalBike(50.877, 4.7085, 5,"Joske", false);
        ;*/

        //enkele testritten aanmaken om te zien of ze verchijnen in firebase  -> dit werkt, nog manier fixen om RentalBikes te linken aan die al in de database zodat we die ook kunnen updaten.
        /*RentalBike fiets1 = new RentalBike(50,5,3, "Albert");
        RentalBike fiets2 = new RentalBike(50,6,4, "Pierre");
        saveNewBikeRide(50,5,51,4, fiets1, 5, 700, 1);
        saveNewBikeRide(50,6,50.5,5.5,fiets2, 13, 10000,2);*/

        //2 nieuwe testritten aanmaken om te testen of ze op de Firebase komen en of we ze kunnen zien

        //saveNewBikeRideExistingBike(50,4,51,5,2,500,1);
        //saveNewBikeRideExistingBike(60,5,69,4,2,600,2);



        //testing bikeRide saving with a bike already existing in the database, selecting on bikeId -> werkt

        //saveNewBikeRideExistingBike(50.8,4.71, 50.9,4.70, 1, 20, 8000, 3);


    }

    public void Scan(View v) {
        Intent lp = new Intent(MainActivity.this, QRActivity.class);
        startActivity(lp);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Leuven and move the camera
        //LatLng gt = new LatLng(50.8748769, 4.70777529999998);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(gt));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        mMap.clear();

        addMarkersToMap(mMap);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        /* //Auto ga naar huidige locatie
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        //TODO check deze gekke error!!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        }*/

        // heb deze updatelocation in een aparte functie geschreven zodat we die opnieuw kunnen oproepen indien nodig  --> check onderaan main
        updateLocation();

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

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
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

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Updating Markers", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        onMapReady(mMap);
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location.getLatitude()+"    "+location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onStop() {
        if (mChildEventListener != null){
            databaseRentalBikeRef.removeEventListener(mChildEventListener);
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+32492693088"));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addMarkersToMap(GoogleMap map){
        mChildEventListener = databaseRentalBikeRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RentalBike tempBike = dataSnapshot.getValue(RentalBike.class);
                double latitude = tempBike.getLatitude();
                double longitude = tempBike.getLongitude();
                int id = tempBike.getBikeId();
                String owner = tempBike.getOwner();
                LatLng location = new LatLng(latitude, longitude);
                map.addMarker(new MarkerOptions().position(location).title("Bike id:"+ id+ "\n Owner: "+owner));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    ValueEventListener markerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            FirebaseMarker tempMarker = dataSnapshot.getValue(FirebaseMarker.class);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("TAG","loadMarker: Cancelled", databaseError.toException());
        }
    };

    public void updateLocation() {

        //Auto ga naar huidige locatie
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        }
    }



    public void addNewRentalBike(double latitude, double longitude, int bikeId, String owner, boolean available){
        RentalBike rentalBike = new RentalBike(latitude,longitude,bikeId,owner, available);
        addRentalBike(rentalBike);

    }

    public void addRentalBike(RentalBike rentalBike){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRentalBikeRef = database.getInstance().getReference("RentalBike");
        mRentalBikeRef.push().setValue(rentalBike);

    }


    public void addNewRentalBikeOnThisLocation(int bikeId, String owner, boolean available){
        updateLocation();
        addNewRentalBike(location.getLatitude(),location.getLongitude(), bikeId, owner, available);
    }


    public void saveNewBikeRideExistingBike(double startLat, double startLng, double endLat, double endLng, int bikeId, int rideDuration, int rideId){
        double distanceRidden = calculateDistance(endLat-startLat,startLng-endLng);
        Query query = databaseRentalBikeRef.orderByChild("bikeId").equalTo(bikeId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d("TAG","We zijn er in geraaakt");
                    for(DataSnapshot data : dataSnapshot.getChildren()){
                        RentalBike rentalBike = data.getValue(RentalBike.class);
                        saveNewBikeRide(startLat,startLng,endLat,endLng,rentalBike,distanceRidden,rideDuration,rideId,rideDuration*costPerS);
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
