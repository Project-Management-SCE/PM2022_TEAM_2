package com.team2.finance.Pages;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.team2.finance.Adapter.NewsAdapter;
import com.team2.finance.Map.AtmMapsActivity;
import com.team2.finance.Map.BanksMapsActivity;
import com.team2.finance.Model.Article;
import com.team2.finance.Model.ResponseModel;
import com.team2.finance.R;
import com.team2.finance.Utility.APIClient;
import com.team2.finance.Utility.APIInterface;
import com.team2.finance.Utility.BaseActivity;
import com.team2.finance.exchnage.Exchange;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    ImageButton iBmenu;
    private static String TAG = "HomeActivity";
    private static final String API_KEY = "29e355ddea64445d8303925e4f0567a7"; //Debug only need to be removed
    public static View.OnClickListener myOnClickListener;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView recyclerView;
    static List<Article> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_home, frameLayout);
        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        //layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        CardView cardView = (CardView) findViewById(R.id.exchange);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Exchange.class);
                startActivity(intent);
            }
        });

        CardView bankCard = (CardView) findViewById(R.id.bank);
        bankCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BanksMapsActivity.class);
                startActivity(intent);
            }
        });

        CardView atmCard = (CardView) findViewById(R.id.atm);
        atmCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AtmMapsActivity.class);
                startActivity(intent);
            }
        });

        final APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ResponseModel> call = apiService.getLatestNews("crypto", API_KEY);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.body().getStatus().equals("ok")) {
                    articleList = response.body().getArticles();
                    if (articleList.size() > 0) {
                        adapter = new NewsAdapter(articleList, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("out", t.toString());
            }
        });

        iBmenu = findViewById(R.id.menu);
        iBmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });

    }

    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);

            String url = articleList.get(selectedItemPosition).getUrl();
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }
}