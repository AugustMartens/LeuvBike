package be.kuleuven.softdev.august.leuvbike;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BikeRideViewActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseRef;
    private String userId;
    private ArrayList<String> array = new ArrayList<>();

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_ride_view);

        listView = (ListView) findViewById(R.id.listview);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference("BikeRides");
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

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

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showData(DataSnapshot dataSnapshot) {

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if(ds.child("userId").getValue(String.class)!=null && ds.child("userId").getValue(String.class).equals(userId)){
                int timeInHours = (ds.child("rideDuration").getValue(int.class))/3600;
                int timeInMinutes = ((ds.child("rideDuration").getValue(int.class))-timeInHours*3600)/60;
                int timeInSeconds = ((ds.child("rideDuration").getValue(int.class))-timeInMinutes*60);

                array.add("Ride ID: " + ds.child("rideId").getValue(int.class) + "\n"
                        + "Starting Coordinates: "+ ds.child("startLat").getValue(double.class) + "    "+ds.child("startLng").getValue(double.class)+"\n"
                        +"Ending Coordinates: "+ds.child("endLat").getValue(double.class)+"    "+ds.child("endLng").getValue(double.class)+"\n"
                        +"Distance Ridden: "+String.format("%.2f",ds.child("distanceRidden").getValue(double.class))+" km"+"\n"
                        +"Cost: "+String.format("%.3f",ds.child("cost").getValue(double.class))+" EUR"+"\n"
                        +"Ride Duration: "+timeInHours+"h"+timeInMinutes+"m"+timeInSeconds+"s"+"\n"
                        +"Used Bike: "+"\n"+"    "+"Bike ID: "+ds.child("bike").child("bikeId").getValue(int.class)+"\n"+"    "
                        +"Bike Owner: "+ds.child("bike").child("owner").getValue(String.class)+"\n");
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            listView.setAdapter(adapter);
        }


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent lp = new Intent(BikeRideViewActivity.this, MainActivity.class);
        startActivity(lp);
    }
}
