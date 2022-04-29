package com.team2.finance.Pages;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team2.finance.Map.BanksMapsActivity;
import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class Profile extends BaseActivity {

    ImageView profile_img;
    TextView name,email_address,phoneNumber;
    ImageButton menu;
    ImageButton edit_bt;
    private FirebaseAuth mAuth;
    private String TAG = "Profile";
    private FirebaseFirestore db;
    private static final int FILE_SELECT_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateFields(currentUser);

        profile_img = (ImageView) findViewById(R.id.profile_img);
        profile_img.setOnClickListener(btnChoosePhotoPressed);
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
    }

    public View.OnClickListener btnChoosePhotoPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent_file = new Intent();
            final int ACTIVITY_SELECT_IMAGE = 1234;
            intent_file.setAction(Intent.ACTION_GET_CONTENT);
            intent_file.setType("image/*");
            startActivityForResult(Intent.createChooser(intent_file, "Select Picture"),ACTIVITY_SELECT_IMAGE);
        }
    };



    private void updateFields(FirebaseUser currentUser){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());
                            name.setText(document.getData().get("first_name").toString()+" "+document.getData().get("last_name").toString());
                            email_address.setText(document.getData().get("email_address").toString());
                            phoneNumber.setText(document.getData().get("phone_number").toString());
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1234:
                if (resultCode == RESULT_OK) {

                    Uri selectedImage = data.getData();
                    final String filePath = selectedImage.getPath();
                    profile_img.setImageURI(null);
                    profile_img.setImageURI(selectedImage);
                }
        }
    };


}