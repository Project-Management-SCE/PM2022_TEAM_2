package com.team2.finance.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

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
import com.team2.finance.Model.AtmObject;
import com.team2.finance.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AtmMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String TAG = AtmMapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private RequestQueue mQueue;
    private String invalidPoints[] = {"33.211031", "34.471063"};
    private List<AtmObject> atmObjectList = new ArrayList<>();
    String url = "https://data.gov.il/api/3/action/datastore_search?resource_id=b9d690de-0a9c-45ef-9ced-3e5957776b26&limit=9999";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_maps);

        mQueue = Volley.newRequestQueue(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_atm_maps);
        mapFragment.getMapAsync(this);
    }


    private void jsonParse() {
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            Float x, y;

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray records = response.getJSONObject("result").getJSONArray("records");

                    for (int i = 0; i < records.length(); i++) {
                        if (!(String.valueOf(records.getJSONObject(i).get("X_Coordinate")).equals("null") && String.valueOf(records.getJSONObject(i).get("Y_Coordinate")).equals("null"))) {
                            if (String.valueOf(records.getJSONObject(i).get("X_Coordinate")).length() == 9 && String.valueOf(records.getJSONObject(i).get("Y_Coordinate")).length() == 9) {
                                if (!String.valueOf(records.getJSONObject(i).get("X_Coordinate")).equals(String.valueOf(records.getJSONObject(i).get("Y_Coordinate")))) {
                                    if (!Arrays.asList(invalidPoints).contains(String.valueOf(records.getJSONObject(i).get("X_Coordinate")))) {
                                        x = Float.valueOf(String.valueOf(records.getJSONObject(i).get("X_Coordinate")));
                                        y = Float.valueOf(String.valueOf(records.getJSONObject(i).get("Y_Coordinate")));
                                        if (Math.round(x) == 35 || Math.round(x) == 36 || Math.round(x) == 34) {
                                            float temp = x;
                                            x = y;
                                            y = temp;
                                        }
                                        AtmObject atmObjectObj = new AtmObject(String.valueOf(records.getJSONObject(i).get("_id")), String.valueOf(records.getJSONObject(i).get("Bank_Name")),
                                                String.valueOf(records.getJSONObject(i).get("ATM_Address")), String.valueOf(records.getJSONObject(i).get("City")), x, y);
                                        atmObjectList.add(atmObjectObj);
                                        LatLng atm = new LatLng(x, y);
                                        Objects.requireNonNull(mMap.addMarker(new MarkerOptions().draggable(true)
                                                .position(atm)
                                                .title(atmObjectObj.getBank_Name() + " - " + atmObjectObj.getCity())))
                                                .setSnippet(atmObjectObj.getBranch_Address());
                                    }
                                }
                            }
                        }
                    }
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
        mQueue.add(request);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        jsonParse();

        mMap.setMinZoomPreference(5.0f);
        mMap.setMaxZoomPreference(12.5f);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.2530, 34.7915), 16.0f));
    }
}

