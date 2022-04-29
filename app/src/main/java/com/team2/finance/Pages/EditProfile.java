package com.team2.finance.Pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team2.finance.Login.LoginFragment;
import com.team2.finance.Login.MainActivity;
import com.team2.finance.R;
import com.team2.finance.Utility.Validation;

import java.util.ArrayList;
import java.util.List;

public class EditProfile extends AppCompatActivity {

    private TextView first_name;
    private TextView last_name;
    private TextView email;
    private String email_c;
    private TextView phone_number;
    private ImageView back_bt;
    private ImageView option_bt;
    private Button apply;
    private FirebaseAuth mAuth;
    private String TAG = "EditProfile";

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
        if (checkValidation()) {
            if (!email.getText().toString().equals(email_c)){
                currentUser.updateEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "email updated");
                        } else {
                            Log.d(TAG, "Error email not updated");
                        }
                    }
                });
            }


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
        }


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
                            email_c=email.getText().toString();
                            phone_number.setText(document.getData().get("phone_number").toString());
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
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




    public AlertDialog DeleteUserDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.dialog_delete_user, null);
        EditText email = (EditText)mView.findViewById(R.id.email_address);
        EditText password = (EditText)mView.findViewById(R.id.password);

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), password.getText().toString());

                        // Prompt the user to re-provide their sign-in credentials
                        user.reauthenticate(credential)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> t) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User account deleted.");

                                                            db.collection("Users").whereEqualTo("Uid", user.getUid())
                                                                    .get()
                                                                    .addOnCompleteListener(task -> {
                                                                        if (task.isSuccessful()) {
                                                                            List<String> list = new ArrayList<>();
                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                db.collection("Users").document(document.getId())
                                                                                        .delete()
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                                                            }
                                                                                        })
                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                Log.w(TAG, "Error deleting document", e);
                                                                                            }
                                                                                        });
                                                                            }

                                                                        } else {
                                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                                        }
                                                                    });

                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                });

                                    }
                                });


                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }


    private void changePassword(FirebaseUser currentUser,String email,String old_password,String new_password){
        AuthCredential credential = EmailAuthProvider.getCredential(email,old_password);
        // Prompt the user to re-provide their sign-in credentials

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            currentUser.updatePassword(new_password).addOnCompleteListener(new OnCompleteListener<Void>() {
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



    public AlertDialog ChangeUserPasswordDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.dialog_update_password, null);
        EditText email = (EditText)mView.findViewById(R.id.email_address);
        EditText old_password = (EditText)mView.findViewById(R.id.old_password);
        EditText new_password = (EditText)mView.findViewById(R.id.new_password);

        builder.setView(mView)
                // Add action buttons
                .setPositiveButton("update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                            AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), old_password.getText().toString());
                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            changePassword(user, email.getText().toString(), old_password.getText().toString(), new_password.getText().toString());
                                        }
                                    });


                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }


    public AlertDialog onCreateDialogOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options")
                .setItems(R.array.edit_profile_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                AlertDialog update_password_dialog = ChangeUserPasswordDialog(null);
                                update_password_dialog.show();
                                break;
                            case 1:
                                AlertDialog delete_user_dialog = DeleteUserDialog(null);
                                delete_user_dialog.show();
                                break;
                            default:
                                //Nothing
                        }
                    }
                });
        return builder.create();
    }


    private boolean checkValidation() {
        boolean validator = true;
        if (!Validation.isAlpha(first_name.getText().toString())) {
            first_name.setError("Invalid Name");
            validator = false;
        }
        if (!Validation.isAlpha(last_name.getText().toString())) {
            last_name.setError("Invalid Name");
            validator = false;
        }
        if (!Validation.isEmailValid(email.getText().toString())) {
            email.setError("Invalid Email Address");
            validator = false;
        }

        if (!Validation.isValidPhoneNumber(phone_number.getText().toString())) {
            phone_number.setError("Invalid phone number");
            validator = false;
        }
        return validator;
    }


    private boolean checkPasswordValidation(EditText new_password) {
        boolean validator = true;

        if (!Validation.isValidPassword(new_password.getText().toString())) {
            new_password.setError("Minimum eight characters, at least one letter and one number");
            validator = false;
        }

        return validator;
    }

}

