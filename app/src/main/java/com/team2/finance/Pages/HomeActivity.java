package com.team2.finance.Pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team2.finance.Adapter.NewsAdapter;
import com.team2.finance.Login.MainActivity;
import com.team2.finance.Map.AtmMapsActivity;
import com.team2.finance.Map.BanksMapsActivity;
import com.team2.finance.Model.Article;
import com.team2.finance.Model.ResponseModel;
import com.team2.finance.R;
import com.team2.finance.Utility.APIClient;
import com.team2.finance.Utility.APIInterface;
import com.team2.finance.Utility.BaseActivity;
import com.team2.finance.exchnage.CryptoExchange;
import com.team2.finance.exchnage.Exchange;

import java.util.ArrayList;
import java.util.Date;
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
    FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = getLayoutInflater().inflate(R.layout.activity_home, frameLayout);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        //layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        checkVIExpireDate();

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

        CardView cryptoCard = (CardView) findViewById(R.id.crypto);
        cryptoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
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


    private void check() {
        if (currentUser != null) {

            db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Boolean vip = (Boolean) (task.getResult().getDocuments().get(0).getData().get("Vip"));
                            if (vip) {
                                Intent intent = new Intent(this, CryptoExchange.class);
                                startActivity(intent);
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                                        .setTitle("You are not VIP member")
                                        .setMessage("Go to payment process?")
                                        .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                                                startActivity(intent);
                                            }
                                        })
                                        //set negative button
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //set what should happen when negative button is clicked
                                            }
                                        })
                                        .show();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("You must login to activate this functionality")
                    .setMessage("Go to login screen?")
                    .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what would happen when positive button is clicked
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    //set negative button
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what should happen when negative button is clicked
                        }
                    })
                    .show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkVIExpireDate() {
        db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());

                            Timestamp timestamp = (Timestamp) document.getData().get("expired");

                            Date date = new Date(timestamp.toDate().getTime());
                            Date date1 = new Date(Timestamp.now().toDate().getTime());
                            long diff = date.getTime() - date1.getTime();
                            if ((diff / (1000 * 60 * 60 * 24) + 1) <= 0) {
                                updateUserStatus();
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateUserStatus() {
        db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());
                            db.collection("Users").document(document.getId()).update(
                                    "Vip", false);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}