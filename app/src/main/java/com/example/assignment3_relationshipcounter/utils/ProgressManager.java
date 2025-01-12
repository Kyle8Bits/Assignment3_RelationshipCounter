package com.example.assignment3_relationshipcounter.utils;

import androidx.fragment.app.FragmentManager;

import com.example.assignment3_relationshipcounter.fragments.ProgressFragment;

public class ProgressManager {
    private static ProgressFragment progressFragment;

    public static void showProgress(FragmentManager fragmentManager) {
        if (progressFragment == null) {
            progressFragment = new ProgressFragment();
            progressFragment.show(fragmentManager, "progress");
        }
    }

    public static void dismissProgress() {
        if (progressFragment != null && progressFragment.isAdded()) {
            progressFragment.dismiss();
            progressFragment = null;
        }
    }
}

