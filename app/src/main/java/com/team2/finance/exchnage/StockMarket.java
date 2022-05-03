package com.team2.finance.exchnage;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.team2.finance.Adapter.StocksAdapter;
import com.team2.finance.Model.Stock;
import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;
import com.team2.finance.Utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class StockMarket extends BaseActivity {

    ImageButton menu;
    RequestQueue requestQueue;
    TextView text;

    ArrayList<String> CommonStocks_symbols = new ArrayList<String>();
    ArrayList<String> CommonStocks_names = new ArrayList<String>();

    //for adapter
    private ArrayList<Stock> StocksList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_stock_market, frameLayout);
        menu = findViewById(R.id.menu);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        recyclerView = findViewById(R.id.recyclerview);
        StocksList = new ArrayList<>();



        updateStocks_symbol_names();
        try {
            getStocks();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setAdapter();



        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void setAdapter()
    {
        StocksAdapter adapter = new StocksAdapter(StocksList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
        DividerItemDecoration.VERTICAL));

        recyclerView.setAdapter(adapter);

    }

    private void updateStocks_symbol_names() {
        CommonStocks_symbols.add("AAPL");
        CommonStocks_symbols.add("AMD");
        CommonStocks_symbols.add("T");
        CommonStocks_symbols.add("F");
        CommonStocks_symbols.add("ITUB");
        CommonStocks_symbols.add("NVDA");
        CommonStocks_symbols.add("NOK");
        CommonStocks_symbols.add("NIO");
        CommonStocks_symbols.add("FB");
        CommonStocks_symbols.add("BAC");
        CommonStocks_symbols.add("UBER");
        CommonStocks_symbols.add("INTC");
        CommonStocks_symbols.add("SOFI");
        CommonStocks_symbols.add("PBR");
        CommonStocks_symbols.add("PLTR");
        CommonStocks_symbols.add("VALE");
        CommonStocks_symbols.add("VICI");
        CommonStocks_symbols.add("OXY");
        CommonStocks_symbols.add("CMCSA");
        CommonStocks_symbols.add("BBD");
        CommonStocks_symbols.add("XOM");
        CommonStocks_symbols.add("PFE");
        CommonStocks_symbols.add("MSFT");
        CommonStocks_symbols.add("NLY");
        CommonStocks_symbols.add("CCL");

        CommonStocks_names.add("Apple Inc");
        CommonStocks_names.add("Advanced Micro Devices, Inc");
        CommonStocks_names.add("AT&T Inc");
        CommonStocks_names.add("Ford Motor Company");
        CommonStocks_names.add("Itaú Unibanco Holding S.A");
        CommonStocks_names.add("NVIDIA Corporation");
        CommonStocks_names.add("Nokia Oyj");
        CommonStocks_names.add("NIO Inc");
        CommonStocks_names.add("Meta Platforms, Inc");
        CommonStocks_names.add("Bank of America Corporation");
        CommonStocks_names.add("Uber Technologies, Inc");
        CommonStocks_names.add("Intel Corporation");
        CommonStocks_names.add("SoFi Technologies, Inc");
        CommonStocks_names.add("Petróleo Brasileiro S.A. - Petrobras");
        CommonStocks_names.add("Palantir Technologies Inc");
        CommonStocks_names.add("Vale S.A");
        CommonStocks_names.add("VICI Properties Inc");
        CommonStocks_names.add("Occidental Petroleum Corporation");
        CommonStocks_names.add("Comcast Corporation");
        CommonStocks_names.add("Banco Bradesco S.A");
        CommonStocks_names.add("Exxon Mobil Corporation");
        CommonStocks_names.add("Pfizer Inc");
        CommonStocks_names.add("Microsoft Corporation");
        CommonStocks_names.add("Annaly Capital Management, Inc");
        CommonStocks_names.add("Carnival Corporation & plc");
    }


//    private void getSymbols()
//    {
//        ArrayList<String> stocks_symbols = new ArrayList<>();
//
//        String url = "https://api.twelvedata.com/stocks?source=docs";
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //create array of all the objects innside coins Jsonfile
//                        JSONArray jsonArray = null;
//                        try {
//                            jsonArray = response.getJSONArray("data");
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject coin = jsonArray.getJSONObject(i);
//                                String symbol = coin.getString("symbol");
//
//                                stocks_symbols.add(symbol);
//
//
//
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.e("initSpinners", "catch inside onResponse function");
//                        }
//
//                        //text.setText(stocks_symbols.get(0));
////
//
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                });
//
//
//        requestQueue.add(jsonObjectRequest);
//
//    }
//
//
//    private void getStock()
//    {
//        ArrayList<String> test = new ArrayList<>();
//        //final String[] test = new String[1];
//
//        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=AAPL&apikey=8YYZIVV9WKAIA4NW";
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        JSONObject jsonObject = null;
//
//                        try {
//                            jsonObject = response.getJSONObject("Meta Data");
//                            test.add(jsonObject.getString("2. Symbol"));
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.e("initSpinners", "catch inside onResponse function");
//                        }
//
//                        //text.setText(test.get(0));
////
//
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                });
//
//
//        requestQueue.add(jsonObjectRequest);
//
//    }



    private void getStocks() throws InterruptedException {


        for(int i = 0 ; i < 4 ; i++)
        {
            //StocksList.add(new Stock(CommonStocks_symbols.get(i) , CommonStocks_names.get(i) , "date" , "rate"));
//            TimeUnit.SECONDS.sleep(1);
//
//
            int finalI = i;
            String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+CommonStocks_symbols.get(finalI)+"&interval=5min&apikey=8YYZIVV9WKAIA4NW";
            //https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=AAPL&interval=5min&apikey=8YYZIVV9WKAIA4NW


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {




                            JSONObject jsonObject = null;
                            Iterator<String> keys = null;
                            String close = null;

                            try {
                                jsonObject = response.getJSONObject("Time Series (5min)");

                                keys = jsonObject.keys();
                                JSONObject obj = jsonObject.getJSONObject(keys.next());
                                close = obj.getString("4. close");

                                StocksList.add(new Stock(CommonStocks_symbols.get(finalI) , CommonStocks_names.get(finalI) , keys.next(), close));
                                Log.e("Stock :" , close);




                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Stocks Array list create Faild", "catch inside onResponse function" ,e);
                            }



                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });


            requestQueue.add(jsonObjectRequest);


        //endfor
        }



    }
}