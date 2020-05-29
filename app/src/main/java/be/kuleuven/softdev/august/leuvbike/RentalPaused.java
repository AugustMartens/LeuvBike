package be.kuleuven.softdev.august.leuvbike;


import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RentalPaused extends AppCompatActivity {
    //private static final boolean AUTO_HIDE = true;

    private double currentprice;
    long elapsedtime;
    public static int status = 0;
    ConstraintLayout myLayout;
    AnimationDrawable animationdrawable;
    double latint;
    double longint;
    private RequestQueue queue;

    private String url;
    private String urlunlock;
    private String urllock;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_paused);
        TextView t = findViewById(R.id.price);
        Bundle extras = getIntent().getExtras();
        currentprice = extras.getDouble("COST");
        elapsedtime = extras.getLong("TIME");
        latint = extras.getDouble("Latint");
        longint = extras.getDouble("Longint");
        url = extras.getString("URL");
        t.setText("CurrentPrice is: " + currentprice + " EUR");

        // coole achtergrond
        myLayout = findViewById(R.id.mylayout);
        animationdrawable = (AnimationDrawable) myLayout.getBackground();
        animationdrawable.setEnterFadeDuration(4500);
        animationdrawable.setExitFadeDuration(4500);
        animationdrawable.start();

        //Declare URLS
        urlunlock ="http://"+url+"/L";
        urllock = "http://"+url+"/H";

        // Lock
        // SEND REQUEST LOCK
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

    public void resume (View v){
        status = 1;
        Log.d("TAG", String.valueOf(status));
        Intent i = new Intent(RentalPaused.this, RentalActivity.class);
        i.putExtra("TIME", elapsedtime);
        i.putExtra("Latint", latint);
        i.putExtra("Longint", longint);
        i.putExtra("URL", url);
        startActivity(i);
        finish();
    }

}
