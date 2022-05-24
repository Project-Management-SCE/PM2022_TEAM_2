package com.team2.finance.exchnage;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.view.Gravity;
import android.view.View;

import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.team2.finance.Adapter.StocksAdapter;
import com.team2.finance.Model.Stock;
import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;
import com.team2.finance.Utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StockMarket extends BaseActivity {

    ImageButton menu;
    RequestQueue requestQueue;


    ArrayList<String> CommonStocks_symbols = new ArrayList<String>();
    ArrayList<String> CommonStocks_names = new ArrayList<String>();

    //for adapter
    private ArrayList<Stock> StocksList;
    private RecyclerView recyclerView;
    private StocksAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_stock_market, frameLayout);
        menu = findViewById(R.id.menu);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        recyclerView = findViewById(R.id.recyclerview);
        StocksList = new ArrayList<>();


        setAdapter();

        updateStocks_symbol_names();
        try {
            getStocks();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void setAdapter() {
        adapter = new StocksAdapter(StocksList);
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

    private void getStocks() throws InterruptedException {


        for (int i = 0; i < CommonStocks_symbols.size(); i++) {
            String url = "https://cloud.iexapis.com/stable/tops?token=pk_e6ffb40cd31543e78dae6296e3e740fa&symbols=" + CommonStocks_symbols.get(i);
            addToStockList(url, i);
        }
    }

    private void addToStockList(String url, int i) {


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            JSONObject obj = (JSONObject) jsonArray.get(0);

                            String data = obj.getString("lastSalePrice");
                            ;
                            StocksList.add(new Stock(CommonStocks_symbols.get(i), CommonStocks_names.get(i), data));
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue.add(jsonArrayRequest);
    }
}