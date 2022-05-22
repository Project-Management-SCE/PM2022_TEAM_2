package com.team2.finance.Pages;

import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Profile extends BaseActivity {

    private String TAG = "Profile";

    ImageView profile_img;
    TextView name, email_address, phoneNumber, date, daysLeft;
    ImageButton menu, cancel, edit_bt;
    LinearLayout vip_layout, cancel_layout;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_profile, frameLayout);


        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateFields(currentUser);

        date = findViewById(R.id.date1);
        daysLeft = findViewById(R.id.date2);

        vip_layout = findViewById(R.id.vip);
        cancel_layout = findViewById(R.id.cancel_view);
        checkIfUserVIP();

        profile_img = (ImageView) findViewById(R.id.profile_img);
        profile_img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog dialog = updateUserImgDialog();
                dialog.show();
            }
        });

        name = (TextView) findViewById(R.id.name);
        email_address = (TextView) findViewById(R.id.email_address);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);

        edit_bt = (ImageButton) findViewById(R.id.edit_bt);
        edit_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
            }
        });

        menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(Profile.this)
                        .setTitle("Cancel subscription")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelSubscription();
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        //set negative button
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set what should happen when negative button is clicked
                            }
                        })
                        .show();
            }
        });
    }

    private void updateFields(FirebaseUser currentUser) {

        db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());
                            name.setText(document.getData().get("first_name").toString() + " " + document.getData().get("last_name").toString());
                            email_address.setText(document.getData().get("email_address").toString());
                            phoneNumber.setText(document.getData().get("phone_number").toString());
                            if (document.getData().get("url") != null) {
                                Glide.with(Profile.this).load(document.getData().get("url").toString()).apply(RequestOptions.circleCropTransform()).into(profile_img);
                            }
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void updateImg(String url) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("Users").whereEqualTo("Uid", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());
                            db.collection("Users").document(document.getId()).update(
                                    "url", url);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public AlertDialog updateUserImgDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.dialog_img_update, null);
        EditText url = (EditText) mView.findViewById(R.id.url);

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (IsValidUrl(url.getText().toString())) {
                            Log.d("url", url.getText().toString());
                            updateImg(url.getText().toString());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                    startActivity(getIntent());
                                }
                            }, 1000);
                        } else {
                            url.setError("Invalid URL");
                        }

                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    public static boolean IsValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return (URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches());
        } catch (MalformedURLException ignored) {
        }
        return false;
    }

    private void checkIfUserVIP() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Boolean vip = (Boolean) (task.getResult().getDocuments().get(0).getData().get("Vip"));
                            if (vip) {
                                updateVIExpireDate(currentUser);
                                vip_layout.setVisibility(View.VISIBLE);
                                cancel_layout.setVisibility(View.VISIBLE);
                            } else {

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateVIExpireDate(FirebaseUser currentUser) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());

                            Timestamp timestamp = (Timestamp) document.getData().get("expired");

                            Date date = new Date(timestamp.toDate().getTime());
                            @SuppressLint("SimpleDateFormat") DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            String formatted = format.format(date);
                            this.date.setText(formatted);

                            Date date1 = new Date(Timestamp.now().toDate().getTime());
                            long diff = date.getTime() - date1.getTime();
                            daysLeft.setText("(" + diff / (1000 * 60 * 60 * 24) + ")");

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void cancelSubscription() {
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