package com.team2.finance.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team2.finance.R;

import java.util.ArrayList;
import java.util.List;

public class EditProfile extends AppCompatActivity {

    private TextView first_name;
    private TextView last_name;
    private TextView old_password;
    private TextView new_password;
    private TextView email;
    private TextView phone_number;
    private ImageView back_bt;
    private ImageView option_bt;
    private Button apply;
    private FirebaseAuth mAuth;
    private String TAG = "EditProfile";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();

        back_bt = findViewById(R.id.back);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });
        option_bt = findViewById(R.id.option);
        option_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = onCreateDialogOption();
                dialog.show();
            }
        });
        first_name = (TextView) findViewById(R.id.firstName_e);
        last_name = (TextView) findViewById(R.id.lastName_e);
        email = (TextView) findViewById(R.id.emailAddress_e);
        phone_number = (TextView) findViewById(R.id.phoneNumber_e);
//        old_password = (TextView) findViewById(R.id.old_password_e);
//        new_password = (TextView) findViewById(R.id.new_password_e);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateFields(currentUser);

        apply = (Button) findViewById(R.id.apply_e);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = onCreateDialogApplyChanges(currentUser);
                dialog.show();
            }
        });
    }


    private void updateDataBaseFields(FirebaseUser currentUser){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());
                            db.collection("Users").document(document.getId()).update(
                                    "first_name", first_name.getText().toString(),
                                    "last_name", last_name.getText().toString(),
                                    "email_address", email.getText().toString(),
                                    "phone_number", phone_number.getText().toString());
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });



//        Intent intent = new Intent(getApplicationContext(), EditProfile.class);
//        startActivity(intent);
    }



    private void updateFields(FirebaseUser currentUser){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("Uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> list = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId());
                            first_name.setText(document.getData().get("first_name").toString());
                            last_name.setText(document.getData().get("last_name").toString());
                            email.setText(document.getData().get("email_address").toString());
                            phone_number.setText(document.getData().get("phone_number").toString());
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void changePassword(FirebaseUser currentUser){
        AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(),old_password.getText().toString());
        // Prompt the user to re-provide their sign-in credentials
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            currentUser.updatePassword(new_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                    } else {
                                        Log.d(TAG, "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed");
                        }
                    }
                });

    }

    private void deleteUser(FirebaseUser currentUser){

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234");

        // Prompt the user to re-provide their sign-in credentials
        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        currentUser.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User account deleted.");
                                        }
                                    }
                                });

                    }
                });
    }

    public AlertDialog onCreateDialogOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options")
                .setItems(R.array.edit_profile_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, ""+which);
                    }
                });
        return builder.create();
    }


    public AlertDialog onCreateDialogApplyChanges(FirebaseUser currentUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit profile");
        builder.setMessage("Are you sure you want to make changes?");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updateDataBaseFields(currentUser);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        return builder.create();
    }
}

