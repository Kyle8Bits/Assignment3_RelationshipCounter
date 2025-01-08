package com.example.assignment3_relationshipcounter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.main_screen.HomeActivity;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class FriendshipDetailFragment extends Fragment {

    private BarChart activityBarChart;
    private MaterialButton backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendship_detail, container, false);

        // Access currentUser from HomeActivity
        HomeActivity homeActivity = (HomeActivity) requireActivity();
        User currentUser = homeActivity.getCurrentUser();

        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Initialize BarChart
        activityBarChart = view.findViewById(R.id.activity_bar_chart);
        setupBarChart();

        // Retrieve arguments
        if (getArguments() != null) {
            TextView currentUserNameTextView = view.findViewById(R.id.current_user_name);
            String friendId = getArguments().getString("friendId");
            String friendName = getArguments().getString("friendName");
            long daysTogether = getArguments().getLong("daysTogether");
            String relationshipId = getArguments().getString("relationshipId");

            // Update UI
            currentUserNameTextView.setText(currentUser.getUsername() + " \uD83D\uDC4B");

            TextView friendNameTextView = view.findViewById(R.id.friend_name);
            friendNameTextView.setText(friendName + " \uD83D\uDC4B");

            TextView daysTogetherTextView = view.findViewById(R.id.days_count);
            daysTogetherTextView.setText("123 Days");

            // Additional logic to load and display relationship/activity details
            loadFriendshipDetails(friendId, relationshipId);
        }

        return view;
    }

    private void loadFriendshipDetails(String friendId, String relationshipId) {
        // Fetch additional details from Firestore or other data sources if needed
        // Implement this based on your app's requirements
    }

    private void setupBarChart() {
        // Create dummy data for the BarChart
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 5)); // Monday
        barEntries.add(new BarEntry(1, 8)); // Tuesday
        barEntries.add(new BarEntry(2, 3)); // Wednesday
        barEntries.add(new BarEntry(3, 6)); // Thursday
        barEntries.add(new BarEntry(4, 9)); // Friday
        barEntries.add(new BarEntry(5, 7)); // Saturday
        barEntries.add(new BarEntry(6, 4)); // Sunday

        // Set up the data set
        BarDataSet barDataSet = new BarDataSet(barEntries, "Activity in the Week");
        barDataSet.setColor(getResources().getColor(R.color.primary)); // Bar color
        barDataSet.setValueTextColor(getResources().getColor(R.color.secondary1)); // Value text color
        barDataSet.setValueTextSize(12f); // Value text size

        // Create BarData
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f); // Bar width

        // Configure the X-Axis
        XAxis xAxis = activityBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(7); // Number of days
        xAxis.setValueFormatter(new ValueFormatter() {
            private final String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

            @Override
            public String getFormattedValue(float value) {
                return days[(int) value];
            }
        });

        // Configure the Y-Axis
        YAxis leftAxis = activityBarChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Start from 0
        activityBarChart.getAxisRight().setEnabled(false); // Disable right Y-Axis

        // Finalize BarChart setup
        activityBarChart.setData(barData);
        activityBarChart.setFitBars(true);
        activityBarChart.getDescription().setEnabled(false); // Disable description text
        activityBarChart.invalidate(); // Refresh the chart
    }
}
