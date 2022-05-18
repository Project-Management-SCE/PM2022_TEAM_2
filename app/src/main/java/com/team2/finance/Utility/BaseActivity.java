package com.team2.finance.Utility;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team2.finance.Login.MainActivity;
import com.team2.finance.Pages.ContactActivity;
import com.team2.finance.Pages.HomeActivity;
import com.team2.finance.Pages.Profile;
import com.team2.finance.R;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected FrameLayout frameLayout;


    private String TAG = "BaseActivity";
    private FirebaseAuth mAuth;
    MenuItem name;
    MenuItem type;
    MenuItem logout;
    MenuItem profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        frameLayout = (FrameLayout) findViewById(R.id.container);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        name = menu.findItem(R.id.name);
        type = menu.findItem(R.id.type);
        logout = menu.findItem(R.id.logout);
        profile = menu.findItem(R.id.profile);

        initUserName();
        initUserType();
    }

    private void initUserName() {

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (currentUser.getUid().equals(String.valueOf(document.getData().get("Uid")))) {
                                    String first_name = (String) document.getData().get("first_name");
                                    String last_name = (String) document.getData().get("last_name");
                                    if (document.getData().get("Vip").toString().equals("true")){
                                        String s = new String(Character.toChars(0x2B50));
                                        name.setTitle(s+first_name + " " + last_name+s);
                                    }
                                    else{
                                        name.setTitle(first_name + " " + last_name);
                                    } }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });

        } else {
            name.setTitle("Error getting Name");
        }
    }

    private void initUserType() {

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                switch (String.valueOf(document.getData().get("type"))) {
                                    case "register":
                                        type.setTitle("register");
                                        break;
                                    case "vip":
                                        type.setTitle("vip");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });

        } else {
            name.setTitle("User not connected");
            logout.setTitle("Go to Login page");
            profile.setVisible(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.profile) {
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        }
        else if ( id == R.id.contact){
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.git) {
            String url = "https://github.com/Project-Management-SCE/PM2022_TEAM_2";
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } else if (id == R.id.logout) {
            SingOut();
        }
        return true;
    }

    public void SingOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}