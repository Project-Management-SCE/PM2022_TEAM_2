package com.team2.finance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.json.*;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private RequestQueue mQueue;
    private FirebaseAuth mAuth;
    private String TAG = MainActivity.class.getSimpleName();
    List<LatLng> points = new ArrayList<>();
    private GoogleMap mMap;

    String url = "https://data.gov.il/api/3/action/datastore_search?resource_id=1c5bc716-8210-4ec7-85be-92e6271955c2";
    String finalUrl;
    String govURL = "https://data.gov.il";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        mQueue = Volley.newRequestQueue(this);
        jsonParse();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    private void jsonParse() {
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            int j = 0;

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("result");
                    Log.e(TAG, "Response from url: " + response);
//                    Log.e("xx", String.valueOf(jsonObject.getJSONObject("_links").get("next")));
//                    while (!String.valueOf(jsonObject.getJSONObject("_links").get("next")).isEmpty())
//                    {
//
//                    }


                    JSONArray jsonArray = jsonObject.getJSONArray("records");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject records = jsonArray.getJSONObject(i);
                        if ((records.has("X_Coordinate") && !records.isNull("X_Coordinate")) && records.has("Y_Coordinate") && !records.isNull("Y_Coordinate")) {
                            if (String.valueOf(records.getDouble("X_Coordinate")).length() == 9 && String.valueOf(records.getDouble("Y_Coordinate")).length() == 9 && (int) records.getDouble("X_Coordinate") !=
                                    35 && (int) records.getDouble("X_Coordinate") !=
                                    34) {
                                points.add(new LatLng(records.getDouble("X_Coordinate"), records.getDouble("Y_Coordinate")));
                                LatLng bank = new LatLng(points.get(j).latitude, points.get(j).longitude);
                                mMap.addMarker(new MarkerOptions()
                                        .position(bank)
                                        .title(String.valueOf(records.getDouble("X_Coordinate"))));
                                j++;

                            }
                        }
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(30.99258, 34.86720)));
                    mMap.setMinZoomPreference(6.0f);
                    mMap.setMaxZoomPreference(14.0f);
                    finalUrl = govURL + String.valueOf(jsonObject.getJSONObject("_links").get("next"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
//        url = finalUrl;
        mQueue.add(request);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}