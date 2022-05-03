package com.team2.finance.Pages;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team2.finance.Map.BanksMapsActivity;
import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;

import java.net.MalformedURLException;
import java.net.URL;
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
        View rootView = getLayoutInflater().inflate(R.layout.activity_profile, frameLayout);


        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateFields(currentUser);

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


    }

//    public View.OnClickListener btnChoosePhotoPressed = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent intent_file = new Intent();
//            final int ACTIVITY_SELECT_IMAGE = 1234;
//            intent_file.setAction(Intent.ACTION_GET_CONTENT);
//            intent_file.setType("image/*");
//            startActivityForResult(Intent.createChooser(intent_file, "Select Picture"),ACTIVITY_SELECT_IMAGE);
//        }
//    };



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
                            if (document.getData().get("url") != null){
                                Glide.with( Profile.this).load(document.getData().get("url").toString()).apply(RequestOptions.circleCropTransform()).into(profile_img);
                            }
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 1234:
//                if (resultCode == RESULT_OK) {
//
//                    Uri selectedImage = data.getData();
//                    final String filePath = selectedImage.getPath();
//                    profile_img.setImageURI(null);
//                    profile_img.setImageURI(selectedImage);
//                }
//        }
//    };



    public void updateImg(String url){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("Uid", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());
                            db.collection("Users").document(document.getId()).update(
                                    "url",url);
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
        EditText url = (EditText)mView.findViewById(R.id.url);

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (IsValidUrl(url.getText().toString())){
                            Log.d("url", url.getText().toString());
                            updateImg(url.getText().toString());
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                    startActivity(getIntent());
                                }
                            }, 1000);
                        }
                        else{
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

}