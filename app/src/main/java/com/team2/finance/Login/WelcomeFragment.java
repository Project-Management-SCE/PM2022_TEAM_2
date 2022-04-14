package com.team2.finance.Login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team2.finance.Pages.HomeActivity;
import com.team2.finance.R;

public class WelcomeFragment extends Fragment implements View.OnClickListener {

    public FirebaseAuth mAuth;
    private FragmentManager fragmentManager;
    private static final String TAG = "LoginFragment";
    private View binding;
    private TextView tvContinue;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflater.inflate(R.layout.fragment_welcome, container, false);
        View view = binding.getRootView();

        Button login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(this);

        Button register = (Button) view.findViewById(R.id.register);
        register.setOnClickListener(this);

        tvContinue = (TextView) view.findViewById(R.id.continue_home);
        tvContinue.setOnClickListener(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return view;
    }

    // constructor
    public WelcomeFragment(FragmentManager fragmentManager) {

        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();
        this.fragmentManager = fragmentManager;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onClick(final View v) { //check for what button is pressed
        switch (v.getId()) {
            case R.id.continue_home:
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.login:
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container, new LoginFragment(fragmentManager))
                        .addToBackStack("Login")
                        .commit();
                break;
            case R.id.register:
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container, new RegisterFragment())
                        .addToBackStack("RegisterRecruiter")
                        .commit();
                break;
        }
    }
}