package com.team2.finance.Map;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.json.*;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team2.finance.Utility.HttpsTrustManager;
import com.team2.finance.Model.AtmObject;
import com.team2.finance.Pages.HomeActivity;
import com.team2.finance.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AtmMapsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap = null;

    private final String TAG = AtmMapsActivity.class.getSimpleName();

    private RequestQueue mQueue;

    private static final LatLng Israel = new LatLng(30.95961, 34.91106);

    private final String[] invalidPoints = {"33.211031", "34.471063"};

    private final List<AtmObject> atmObjectList = new ArrayList<>();

    String url = "https://data.gov.il/api/3/action/datastore_search?resource_id=b9d690de-0a9c-45ef-9ced-3e5957776b26&limit=9999";

    private Spinner ATMsFilterSpinner;
    private Spinner ATMsSpinnerBankNames;

    LatLng selectedCity = Israel;


    private static final int[] citiesOnAtmSpinner = {
            R.string.Default_City,
            R.string.Beer_Sheva,
            R.string.Tel_Aviv,
            R.string.Jerusalem,
            R.string.Haifa,
            R.string.Rishon,
            R.string.P_tikva,
            R.string.Ashdod,
            R.string.Netanaya,
            R.string.Eilat,
            R.string.Holon
    };

    private static final int[] banksOnAtmSpinner = {
            R.string.Default_Bank,
            R.string.union_bank,
            R.string.bank_ostar,
            R.string.bank_yahav,
            R.string.bank_jerusalem,
            R.string.mizrahi_tefahot,
            R.string.leumi_bank,
            R.string.poalim_bank,
            R.string.mercantile_bank,
            R.string.yovnak_bank,
    };

    HashMap<Integer, String> numCityHashMap = new HashMap<Integer, String>();
    Map<Integer, List<Double>> numLatLngHashMap = new HashMap<Integer, List<Double>>();
    HashMap<Integer, String> numBanksHashMap = new HashMap<Integer, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_maps);

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        ATMsFilterSpinner = findViewById(R.id.citiesSpinner);
        ATMsFilterSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(citiesOnAtmSpinner)));

        ATMsSpinnerBankNames = findViewById(R.id.banksSpinner);
        ATMsSpinnerBankNames.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(banksOnAtmSpinner)));

        numToCityHashMap();
        numToLatLngHashMap();
        numToBanksHashMap();


        mQueue = Volley.newRequestQueue(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_atm_maps);
        mapFragment.getMapAsync(this);
    }

    public void numToCityHashMap() {
        numCityHashMap.put(0, "הכל");
        numCityHashMap.put(1, "באר שבע");
        numCityHashMap.put(2, "תל אביב -יפו");
        numCityHashMap.put(3, "ירושלים");
        numCityHashMap.put(4, "חיפה");
        numCityHashMap.put(5, "ראשון לציון");
        numCityHashMap.put(6, "פתח תקווה");
        numCityHashMap.put(7, "אשדוד");
        numCityHashMap.put(8, "נתניה");
        numCityHashMap.put(9, "אילת");
        numCityHashMap.put(10, "חולון");
    }

    public void numToLatLngHashMap() {
        numLatLngHashMap.put(0, new ArrayList<Double>(Arrays.asList(30.95961, 34.91106)));
        numLatLngHashMap.put(1, new ArrayList<Double>(Arrays.asList(31.25232, 34.80531)));
        numLatLngHashMap.put(2, new ArrayList<Double>(Arrays.asList(32.08413, 34.77536)));
        numLatLngHashMap.put(3, new ArrayList<Double>(Arrays.asList(31.76823, 35.22582)));
        numLatLngHashMap.put(4, new ArrayList<Double>(Arrays.asList(32.79146, 34.99857)));
        numLatLngHashMap.put(5, new ArrayList<Double>(Arrays.asList(31.97155, 34.79623)));
        numLatLngHashMap.put(6, new ArrayList<Double>(Arrays.asList(32.08146, 34.88976)));
        numLatLngHashMap.put(7, new ArrayList<Double>(Arrays.asList(31.80705, 34.65712)));
        numLatLngHashMap.put(8, new ArrayList<Double>(Arrays.asList(32.32038, 34.84665)));
        numLatLngHashMap.put(9, new ArrayList<Double>(Arrays.asList(29.55845, 34.95132)));
        numLatLngHashMap.put(10, new ArrayList<Double>(Arrays.asList(32.01484, 34.78807)));
    }

    public void numToBanksHashMap() {
        numBanksHashMap.put(0, "הכל");
        numBanksHashMap.put(1, "בנק אגוד לישראל בע\"מ");
        numBanksHashMap.put(2, "בנק אוצר החייל בע\"מ");
        numBanksHashMap.put(3, "בנק יהב לעובדי המדינה בע\"מ");
        numBanksHashMap.put(4, "בנק ירושלים בע\"מ");
        numBanksHashMap.put(5, "בנק מזרחי טפחות בע\"מ");
        numBanksHashMap.put(6, "בנק לאומי לישראל בע\"מ");
        numBanksHashMap.put(7, "בנק הפועלים בע\"מ");
        numBanksHashMap.put(8, "בנק פועלי אגודת ישראל בע\"מ");
        numBanksHashMap.put(9, "בנק מרכנתיל דיסקונט בע\"מ");
        numBanksHashMap.put(10, "יובנק בע\"מ");
    }

    private String[] getResourceStrings(int[] resourceIds) {
        String[] strings = new String[resourceIds.length];
        for (int i = 0; i < resourceIds.length; i++) {
            strings[i] = getString(resourceIds[i]);
        }
        return strings;
    }


    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void jsonParse() {
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url,
                null, new com.android.volley.Response.Listener<JSONObject>() {
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
                                                .icon(vectorToBitmap(R.drawable.ic_baseline_local_atm_24, Color.parseColor("#F3B533")))
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
        Log.e(TAG, "jsonParse: finished");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ATMsFilterSpinner.setOnItemSelectedListener(this);
        ATMsSpinnerBankNames.setOnItemSelectedListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Israel, 7));
        jsonParse();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {

            case R.id.citiesSpinner:
                if (pos != 0) {
                    cityMarker(numCityHashMap.get(pos));
                    List<Double> LatLngValues = numLatLngHashMap.get(pos);
                    assert LatLngValues != null;
                    selectedCity = new LatLng(LatLngValues.get(0), LatLngValues.get(1));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedCity, 12));
                } else {
                    selectedCity = Israel;
                    resetMap(selectedCity);
                }
                break;

            case R.id.banksSpinner:
                if (pos != 0) {
                    BankMarker(numBanksHashMap.get(pos));
                    if (selectedCity != Israel) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedCity, 12));
                    } else
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Israel, 7));
                } else {
                    selectedCity = Israel;
                    resetMap(selectedCity);
                }
                break;
        }
    }


    public void resetMap(LatLng selectedCity) {
        mMap.clear();
        LatLng atm;
        for (int j = 0; j < atmObjectList.size(); j++) {
                atm = new LatLng(atmObjectList.get(j).getX_Coordinate(), atmObjectList.get(j).getY_Coordinate());
                Objects.requireNonNull(mMap.addMarker(new MarkerOptions().draggable(true)
                        .position(atm)
                        .icon(vectorToBitmap(R.drawable.ic_baseline_local_atm_24, Color.parseColor("#F3B533")))
                        .title(atmObjectList.get(j).getBank_Name() + " - " + atmObjectList.get(j).getCity())))
                        .setSnippet(atmObjectList.get(j).getBranch_Address());
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedCity, 7));
    }

    public void cityMarker(String cityName) {
        mMap.clear();
        LatLng atm;
        for (int j = 0; j < atmObjectList.size(); j++) {
            if (atmObjectList.get(j).getCity().equals(cityName)) {
                atm = new LatLng(atmObjectList.get(j).getX_Coordinate(), atmObjectList.get(j).getY_Coordinate());
                Objects.requireNonNull(mMap.addMarker(new MarkerOptions().draggable(true)
                        .position(atm)
                        .icon(vectorToBitmap(R.drawable.ic_baseline_local_atm_24, Color.parseColor("#F3B533")))
                        .title(atmObjectList.get(j).getBank_Name() + " - " + atmObjectList.get(j).getCity())))
                        .setSnippet(atmObjectList.get(j).getBranch_Address());
            }
        }
    }

    public void BankMarker(String bankName) {
        mMap.clear();
        LatLng atm;
        for (int j = 0; j < atmObjectList.size(); j++) {
            if (atmObjectList.get(j).getBank_Name().equals(bankName)) {
                atm = new LatLng(atmObjectList.get(j).getX_Coordinate(), atmObjectList.get(j).getY_Coordinate());
                Objects.requireNonNull(mMap.addMarker(new MarkerOptions().draggable(true)
                        .position(atm)
                        .icon(vectorToBitmap(R.drawable.ic_baseline_local_atm_24, Color.parseColor("#F3B533")))
                        .title(atmObjectList.get(j).getBank_Name() + " - " + atmObjectList.get(j).getCity())))
                        .setSnippet(atmObjectList.get(j).getBranch_Address());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Don't do anything here.
    }
}

