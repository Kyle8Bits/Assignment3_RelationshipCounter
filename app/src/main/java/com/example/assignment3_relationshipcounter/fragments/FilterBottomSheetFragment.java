package com.example.assignment3_relationshipcounter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment3_relationshipcounter.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private FilterCallback callback;

    public interface FilterCallback {
        void onFilterByLatestDays();
        void onFilterByOldestDays();
        void onFilterByNameAZ();
        void onFilterByNameZA();
    }

    public void setFilterCallback(FilterCallback callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_bottom_sheet, container, false);

        // Set up filter buttons
        view.findViewById(R.id.filter_by_latest_days).setOnClickListener(v -> {
            if (callback != null) callback.onFilterByLatestDays();
            dismiss();
        });

        view.findViewById(R.id.filter_by_oldest_days).setOnClickListener(v -> {
            if (callback != null) callback.onFilterByOldestDays();
            dismiss();
        });

        view.findViewById(R.id.filter_by_name_az).setOnClickListener(v -> {
            if (callback != null) callback.onFilterByNameAZ();
            dismiss();
        });

        view.findViewById(R.id.filter_by_name_za).setOnClickListener(v -> {
            if (callback != null) callback.onFilterByNameZA();
            dismiss();
        });

        return view;
    }
}
