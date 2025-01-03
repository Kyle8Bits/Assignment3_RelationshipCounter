package com.example.assignment3_relationshipcounter.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.main_screen.HomeActivity;
import com.example.assignment3_relationshipcounter.service.ProgressManager;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private Authentication auth;
    private DataUtils dataUtils;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        EditText eEmail = view.findViewById(R.id.e_email);
        EditText ePassword = view.findViewById(R.id.e_password);
        Button loginBtn = view.findViewById(R.id.action_login);
        ImageView backBtn = view.findViewById(R.id.button_back);
        TextView errorDisplay = view.findViewById(R.id.error_message);
        auth = new Authentication();

        // Back to welcome
        backBtn.setOnClickListener(v -> {
            getChildFragmentManager().popBackStack();
            requireActivity().findViewById(R.id.auth_container).setVisibility(View.INVISIBLE);

        });

        // Login process
        loginBtn.setOnClickListener(v -> {
            ProgressManager.showProgress(getChildFragmentManager());
            String email = eEmail.getText().toString();
            String password = ePassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                errorDisplay.setText("Please enter all fields");
            }
            Toast.makeText(requireActivity(), "Reach login", Toast.LENGTH_SHORT).show();
            auth.login(email, password, new Authentication.LoginCallback() {

                @Override
                public void onSuccess(FirebaseUser user) {

                    // Get the user object in firestore
                    dataUtils = new DataUtils();
                    dataUtils.getById("users", user.getUid(), User.class, new DataUtils.FetchCallback<User>() {
                        @Override
                        public void onSuccess(User data) {
                            Toast.makeText(requireActivity(), "Reach data", Toast.LENGTH_SHORT).show();
                            // Navigate to Home
                            ProgressManager.dismissProgress();
                            Intent intent = new Intent(requireActivity(), HomeActivity.class);
                            intent.putExtra("currentUser", user);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            ProgressManager.dismissProgress();
                            errorDisplay.setText(e.getMessage());
                        }
                    });

                }

                @Override
                public void onFailure(Exception e) {
                    ProgressManager.dismissProgress();
                    errorDisplay.setText(e.getMessage());
                }
            });
        });


        return view;
    }
}