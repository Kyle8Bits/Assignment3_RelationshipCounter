package com.example.assignment3_relationshipcounter.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.FriendList;
import com.example.assignment3_relationshipcounter.adapter.StoryList;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.utils.UserSession;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView storyRecyclerView;
    private RecyclerView tabRecyclerView;
    private TabLayout tabLayout;

    private FriendList myFriendsAdapter;
    private FriendList requestAdapter;

    private List<User> myFriendsList;
    private List<User> requestList;

    private MaterialButton searchButton;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        storyRecyclerView = view.findViewById(R.id.home_story_list);
        tabRecyclerView = view.findViewById(R.id.home_recycler_view);
        tabLayout = view.findViewById(R.id.home_tab_layout);
        searchButton = view.findViewById(R.id.search_button);

        // Load Stories
        loadStories();

        // Load Tab Content
        setupTabs();

        // Set up click listener for the search button
        searchButton.setOnClickListener(v -> navigateToSearchFriendFragment());

        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // Log the selected tab position
//        int selectedTabPosition = tabLayout.getSelectedTabPosition();
//        Log.d("HomeFragment", "Selected Tab Position: " + selectedTabPosition);
//
//        // Force adapter reset and refresh data
//        if (selectedTabPosition == 0) {
//            tabRecyclerView.setAdapter(myFriendsAdapter);
//            fetchMyFriends();
//            Log.d("HomeFragment", "Selected Tab Position: " + selectedTabPosition);
//        } else {
//            tabRecyclerView.setAdapter(requestAdapter);
//            fetchFriendRequests();
//        }
//    }


    private void navigateToSearchFriendFragment() {
        // Create a new instance of SearchFriendFragment
        SearchFriendFragment searchFriendFragment = new SearchFriendFragment();

        // Replace the current fragment with SearchFriendFragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, searchFriendFragment);
        transaction.addToBackStack(null); // Add to back stack to allow user to navigate back
        transaction.commit();
    }

    private void loadStories() {
        StoryList storyAdapter = new StoryList(requireContext());
        storyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        storyRecyclerView.setAdapter(storyAdapter);
    }

    private void setupTabs() {
        // Initialize the lists for My Friends and Explore
        myFriendsList = new ArrayList<>();
        requestList = new ArrayList<>();

        // Initialize adapters
        myFriendsAdapter = new FriendList(requireContext(), myFriendsList);
        requestAdapter = new FriendList(requireContext(), requestList);
        fetchFriendRequests();

        // Set RecyclerView properties
        tabRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set default tab
        tabLayout.selectTab(tabLayout.getTabAt(0));

        // Fetch Data for My Friends Tab
        tabRecyclerView.setAdapter(myFriendsAdapter);
        fetchMyFriends();


        // Add Tab Selection Logic
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Switch to My Friends Tab
                    tabRecyclerView.setAdapter(myFriendsAdapter);
                    fetchMyFriends();
                } else if (tab.getPosition() == 1) {
                    // Switch to Explore Tab
                    tabRecyclerView.setAdapter(requestAdapter);
                    fetchFriendRequests();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Force refresh when a tab is reselected
                if (tab.getPosition() == 0) {
                    fetchMyFriends();
                } else if (tab.getPosition() == 1) {
                    fetchFriendRequests();
                }
            }
        });
    }


    private void fetchMyFriends() {
        DataUtils dataUtils = new DataUtils();
        String currentUserId = UserSession.getInstance().getCurrentUser().getId();

        dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
            @Override
            public void onSuccess(List<Relationship> relationships) {
                List<String> friendIds = new ArrayList<>();
                for (Relationship relationship : relationships) {
                    if (relationship.getStatus() == FriendStatus.FRIEND &&
                            (relationship.getFirstUser().equals(currentUserId) || relationship.getSecondUser().equals(currentUserId))) {
                        friendIds.add(relationship.getFirstUser().equals(currentUserId)
                                ? relationship.getSecondUser()
                                : relationship.getFirstUser());
                    }
                }

                dataUtils.getAll("users", User.class, new DataUtils.FetchCallback<List<User>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<User> allUsers) {
                        myFriendsList.clear(); // Clear old data
                        for (User user : allUsers) {
                            if (friendIds.contains(user.getId())) {
                                myFriendsList.add(user); // Add friends to the list

                                // Log friend details
                                Log.d("FetchMyFriends", "Friend: " + user.getUsername() + " (ID: " + user.getId() + ")");
                            }
                        }

                        // Log total number of friends
                        Log.d("FetchMyFriends", "Total Friends: " + myFriendsList.size());

                        myFriendsAdapter.updateList(myFriendsList); // Update the adapter
                        myFriendsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("FetchMyFriends", "Failed to fetch users", e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FetchMyFriends", "Failed to fetch relationships", e);
            }
        });
    }

    private void fetchFriendRequests() {
        DataUtils dataUtils = new DataUtils();
        String currentUserId = UserSession.getInstance().getCurrentUser().getId();

        dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
            @Override
            public void onSuccess(List<Relationship> relationships) {
                List<String> requestSenderIds = new ArrayList<>();

                for (Relationship relationship : relationships) {
                    // Check if the current user is the receiver and the status is pending
                    if (relationship.getSecondUser().equals(currentUserId) && relationship.getStatus() == FriendStatus.PENDING) {
                        requestSenderIds.add(relationship.getFirstUser());
                    }
                }

                dataUtils.getAll("users", User.class, new DataUtils.FetchCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> allUsers) {
                        requestList.clear(); // Clear the current list
                        for (User user : allUsers) {
                            if (requestSenderIds.contains(user.getId())) {
                                requestList.add(user); // Add users who sent friend requests
                            }
                        }
                        requestAdapter.updateList(requestList); // Update the adapter
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
