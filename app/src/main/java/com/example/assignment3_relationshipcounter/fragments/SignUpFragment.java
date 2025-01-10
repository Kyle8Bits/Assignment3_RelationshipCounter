package com.example.assignment3_relationshipcounter.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.main_screen.HomeActivity;
import com.example.assignment3_relationshipcounter.service.ProgressManager;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.models.Gender;
import com.example.assignment3_relationshipcounter.service.models.Image;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.service.models.UserType;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SignUpFragment extends Fragment {
    private TextView eDOB;
    private TextView selectedGender;
    Authentication auth;


    public SignUpFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        EditText eFirstName = view.findViewById(R.id.e_first_name);
        EditText eLastName = view.findViewById(R.id.e_last_name);
        EditText eUsername = view.findViewById(R.id.e_username);
        EditText eEmail = view.findViewById(R.id.e_email);
        EditText ePassword = view.findViewById(R.id.e_password);
        TextView maleOption = view.findViewById(R.id.male);
        maleOption.setOnClickListener(v -> selectGender(maleOption));
        TextView femaleOption = view.findViewById(R.id.female);
        femaleOption.setOnClickListener(v -> selectGender(femaleOption));
        TextView otherOption = view.findViewById(R.id.other);
        otherOption.setOnClickListener(v -> selectGender(otherOption));
        EditText ePhone = view.findViewById(R.id.e_phone_number);
        eDOB = view.findViewById(R.id.e_dob);
        eDOB.setOnClickListener(v -> pickDOB());

        ImageView backBtn = view.findViewById(R.id.button_back);
        backBtn.setOnClickListener(v -> {
            getChildFragmentManager().popBackStack();
            requireActivity().findViewById(R.id.auth_container).setVisibility(View.INVISIBLE);
        });


        TextView errorDisplay = view.findViewById(R.id.error_message);
        Button signUpBtn = view.findViewById(R.id.action_signup);
        auth = new Authentication();
        signUpBtn.setOnClickListener(v -> {
            Toast.makeText(requireActivity(), "Reach sign up", Toast.LENGTH_SHORT).show();
//            ProgressManager.showProgress(getChildFragmentManager());

            // Retrieve user input
            String firstName = eFirstName.getText().toString();
            Toast.makeText(requireActivity(), firstName, Toast.LENGTH_SHORT).show();
            String lastName = eLastName.getText().toString();
            String username = eUsername.getText().toString();
            String email = eEmail.getText().toString();
            String password = ePassword.getText().toString();
            String phoneNumber = ePhone.getText().toString();
            String dob = eDOB.getText().toString();
            Toast.makeText(requireActivity(), dob, Toast.LENGTH_SHORT).show();
            // Validate input fields
            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty()
                    || password.isEmpty() || phoneNumber.isEmpty() || dob.isEmpty() || selectedGender.getText().toString().isEmpty()) {
//                ProgressManager.dismissProgress();
                errorDisplay.setText("Please fill in all fields");
                return;
            }
            // Create new user object
            Gender gender = Gender.fromString(selectedGender.getText().toString().toLowerCase());

            User newUser = new User("", firstName, lastName, username, email, dob, gender, phoneNumber, UserType.USER, 0, 0);
            // Register user
            auth.registerNewUser(email, password, newUser, new Authentication.RegisterNewUserCallback() {

                @Override
                public void onSuccess() {
                    ProgressManager.dismissProgress();
                    Toast.makeText(requireActivity(), "Reach success", Toast.LENGTH_SHORT).show();
                    // Navigate to HomeActivity
                    Intent intent = new Intent(requireActivity(), HomeActivity.class);
                    intent.putExtra("currentUser", newUser);
                    startActivity(intent);
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
    private void selectGender(TextView selected) {
        if (selectedGender != null) {
            selectedGender.setSelected(false);
        }
        selected.setSelected(true);
        selectedGender = selected;
    }

    private void pickDOB() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String date = formatter.format(new Date(selection));
            eDOB.setText(date);
        });

        datePicker.show(requireActivity().getSupportFragmentManager(), "tag");
    }
}