package com.team2.finance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        Log.e(TAG, String.valueOf(mAuth.getCurrentUser()));

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, WelcomePage.class);
            startActivity(intent);
        } else {
            // TODO
            FirebaseAuth.getInstance().signOut(); // Debug
        }

    }
}