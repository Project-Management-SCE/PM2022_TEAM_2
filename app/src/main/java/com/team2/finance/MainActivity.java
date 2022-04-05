package com.team2.finance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //super.onCreate(savedInstanceState);
        //View rootView = getLayoutInflater().inflate(R.layout.activity_main, frameLayout);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) { // User not exist
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.main_container, new welcomeFragment(fragmentManager))
                    .commit();
        } else { // User exist check fot type and start the necessary activity
            Log.d(TAG, currentUser.getUid());
        }

    }
}