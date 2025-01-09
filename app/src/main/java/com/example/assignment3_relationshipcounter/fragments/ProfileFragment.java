package com.example.assignment3_relationshipcounter.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.main_screen.WelcomeActivity;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        Button logOutBtn = view.findViewById(R.id.action_logout);

        logOutBtn.setOnClickListener(v -> logout());

        return view;
    }

    private void logout() {
        // Navigate back to main
        Authentication auth = new Authentication();
        auth.logout();
        Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
        // Clear back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}