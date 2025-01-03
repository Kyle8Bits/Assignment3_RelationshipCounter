package com.example.assignment3_relationshipcounter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.example.assignment3_relationshipcounter.R;

public class ProgressFragment extends DialogFragment {

    public ProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        // Prevent dismissing manually
        setCancelable(false);
        return view;
    }
}