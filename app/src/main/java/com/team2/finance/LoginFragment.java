package com.team2.finance;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private FirebaseAuth mAuth;
    private static final String TAG = "Login";
    private View binding;

    private TextView password;
    private TextView email;
    private TextView forgot_password;

    public LoginFragment(FragmentManager fragmentManager) {
        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = inflater.inflate(R.layout.fragment_login, container, false);
        View view = binding.getRootView();

        password = (TextView) view.findViewById(R.id.user_password);
        email = (TextView) view.findViewById(R.id.email_address);

        Button login = (Button) view.findViewById(R.id.login);
        forgot_password = (TextView) view.findViewById(R.id.forgot_password);

        ImageButton back = (ImageButton) view.findViewById(R.id.back);
        back.setOnClickListener(this);

        login.setOnClickListener(this);
        forgot_password.setOnClickListener(this);


        return view;
    }

    private boolean checkValidation() {
        boolean validator = true;
        if (!Validation.isEmailValid(email.getText().toString())) {
            email.setError("Invalid Email Address");
            validator = false;
        }
        if (!Validation.isEmpty(password.getText().toString())) {
            password.setError("Invalid password");
            validator = false;
        }
        return validator;
    }

    void login() {
        String user_email, user_password;
        user_email = email.getText().toString();
        user_password = password.getText().toString();

        mAuth.signInWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.login:
                if (checkValidation()) {
                    login();
                }
                break;
            case R.id.forgot_password:
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container, new ForgotPasswordFragment(fragmentManager))
                        .addToBackStack("ForgotPassword")
                        .commit();
                break;
        }
    }
}