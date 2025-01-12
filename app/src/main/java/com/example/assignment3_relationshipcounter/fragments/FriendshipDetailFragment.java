package com.example.assignment3_relationshipcounter.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.main_screen.HomeActivity;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendshipDetailFragment extends Fragment {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private BarChart activityBarChart;
    private MaterialButton backButton, optionButton, viewGalleryButton;
    private MaterialButton callButton;
    private TextView daysCountTextView;
    private User currentUser;
    private DataUtils dataUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendship_detail, container, false);

        // Initialize UI components
        initializeUI(view);

        // Access current user from HomeActivity
        HomeActivity homeActivity = (HomeActivity) requireActivity();
        currentUser = homeActivity.getCurrentUser();

        // Retrieve arguments
        if (getArguments() != null) {
            handleFragmentArguments(view);
        }

        return view;
    }

    private void initializeUI(View view) {
        backButton = view.findViewById(R.id.back_button);
        optionButton = view.findViewById(R.id.option_button);
//        callButton = view.findViewById(R.id.call_button);
        viewGalleryButton = view.findViewById(R.id.view_gallery_button);
        daysCountTextView = view.findViewById(R.id.days_count);
        activityBarChart = view.findViewById(R.id.activity_bar_chart);

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        // Set up "View Gallery" button click listener
        viewGalleryButton.setOnClickListener(v -> navigateToGallery());
//        setupCallButton();
        setupOptionButton();
        setupBarChart();
    }

    private void navigateToGallery() {
        if (getArguments() != null) {
            String relationshipId = getArguments().getString("relationshipId");

            if (relationshipId != null) {
                // Navigate to GalleryFragment
                GalleryFragment galleryFragment = new GalleryFragment();

                Bundle args = new Bundle();
                args.putString("relationshipId", relationshipId);
                galleryFragment.setArguments(args);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, galleryFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(requireContext(), "Invalid relationship ID.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleFragmentArguments(View view) {
        TextView currentUserNameTextView = view.findViewById(R.id.current_user_name);
        TextView friendNameTextView = view.findViewById(R.id.friend_name);

        String friendId = getArguments().getString("friendId");
        String friendName = getArguments().getString("friendName");
        String relationshipId = getArguments().getString("relationshipId");

        currentUserNameTextView.setText(currentUser.getUsername() + " \uD83D\uDC4B");
        friendNameTextView.setText(friendName + " \uD83D\uDC4B");

        // Load friendship details from Firestore
        loadFriendshipDetails(relationshipId);
    }


    private void setupOptionButton() {
        optionButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), optionButton);
            popupMenu.inflate(R.menu.friendship_detail_menu);

            // Apply the custom style
            popupMenu.setForceShowIcon(true); // Optional, to show icons if added in the future
            try {
                Field mPopup = popupMenu.getClass().getDeclaredField("mPopup");
                mPopup.setAccessible(true);
                Object menuPopupHelper = mPopup.get(popupMenu);
                Class<?> popupHelperClass = Class.forName(menuPopupHelper.getClass().getName());
                Method setStyle = popupHelperClass.getDeclaredMethod("setStyle", Context.class, int.class);
                setStyle.invoke(menuPopupHelper, requireContext(), R.style.CustomPopupMenu);
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupMenu.setOnMenuItemClickListener(this::onMenuItemClick);
            popupMenu.show();
        });
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        String relationshipId = getArguments().getString("relationshipId");
        switch (menuItem.getItemId()) {
            case R.id.action_unfriend:
                handleUnfriendAction(relationshipId);
                return true;
            case R.id.action_block:
                handleBlockAction(relationshipId);
                return true;
            default:
                return false;
        }
    }

    private void handleUnfriendAction(String relationshipId) {
        if (relationshipId == null) {
            Toast.makeText(requireContext(), "Invalid relationship ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        dataUtils = new DataUtils();
        dataUtils.deleteById("relationships", relationshipId, new DataUtils.NormalCallback<Void>() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Successfully unfriended.", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to unfriend.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBlockAction(String relationshipId) {
        if (relationshipId == null) {
            Toast.makeText(requireContext(), "Invalid relationship ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        dataUtils = new DataUtils();
        dataUtils.getById("relationships", relationshipId, Relationship.class, new DataUtils.FetchCallback<Relationship>() {
            @Override
            public void onSuccess(Relationship relationship) {
                relationship.setStatus(FriendStatus.BLOCKED);
                dataUtils.updateRelationship(relationship, new DataUtils.NormalCallback<Void>() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(requireContext(), "Friend has been blocked.", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(requireContext(), "Failed to block friend.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to block friend.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFriendshipDetails(String relationshipId) {
        if (relationshipId == null) {
            Toast.makeText(requireContext(), "Invalid relationship ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        dataUtils = new DataUtils();
        dataUtils.getById("relationships", relationshipId, Relationship.class, new DataUtils.FetchCallback<Relationship>() {
            @Override
            public void onSuccess(Relationship relationship) {
                long daysTogether = calculateDaysTogether(relationship.getDateCreated());
                daysCountTextView.setText(daysTogether + " Days");
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load relationship details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private long calculateDaysTogether(Timestamp dateCreated) {
        if (dateCreated == null) {
            return 0;
        }

        Date creationDate = dateCreated.toDate();
        Date currentDate = new Date();
        long differenceInMilliseconds = currentDate.getTime() - creationDate.getTime();
        return differenceInMilliseconds / (1000 * 60 * 60 * 24); // Convert milliseconds to days
    }

    private void setupBarChart() {
        List<BarEntry> barEntries = createDummyData();

        BarDataSet barDataSet = new BarDataSet(barEntries, "Activity in the Week");
        barDataSet.setColor(getResources().getColor(R.color.primary));
        barDataSet.setValueTextColor(getResources().getColor(R.color.secondary1));
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);

        XAxis xAxis = activityBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

            @Override
            public String getFormattedValue(float value) {
                return days[(int) value];
            }
        });

        YAxis leftAxis = activityBarChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        activityBarChart.getAxisRight().setEnabled(false);

        activityBarChart.setData(barData);
        activityBarChart.setFitBars(true);
        activityBarChart.getDescription().setEnabled(false);
        activityBarChart.invalidate();
    }

    private List<BarEntry> createDummyData() {
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 5)); // Monday
        barEntries.add(new BarEntry(1, 8)); // Tuesday
        barEntries.add(new BarEntry(2, 3)); // Wednesday
        barEntries.add(new BarEntry(3, 6)); // Thursday
        barEntries.add(new BarEntry(4, 9)); // Friday
        barEntries.add(new BarEntry(5, 7)); // Saturday
        barEntries.add(new BarEntry(6, 4)); // Sunday
        return barEntries;
    }

    private void makeCall(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission granted. Please press the call button again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "CALL_PHONE permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
