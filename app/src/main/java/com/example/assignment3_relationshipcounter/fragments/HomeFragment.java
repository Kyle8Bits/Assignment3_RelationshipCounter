package com.example.assignment3_relationshipcounter.fragments;

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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView storyRecyclerView;
    private RecyclerView tabRecyclerView;
    private TabLayout tabLayout;

    private FriendList myFriendsAdapter;
    private FriendList exploreAdapter;

    private List<User> myFriendsList;
    private List<User> exploreList;

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
        exploreList = new ArrayList<>();

        // Initialize adapters
        myFriendsAdapter = new FriendList(requireContext(), myFriendsList);
        exploreAdapter = new FriendList(requireContext(), exploreList);

        // Set RecyclerView properties
        tabRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        tabRecyclerView.setAdapter(myFriendsAdapter); // Default to My Friends

        // Fetch Data for My Friends Tab
        fetchMyFriends();

        // Fetch Data for Explore Tab
        fetchExploreUsers();

        // Add Tab Selection Logic
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Switch to My Friends Tab
                    tabRecyclerView.setAdapter(myFriendsAdapter);
                } else if (tab.getPosition() == 1) {
                    // Switch to Explore Tab
                    tabRecyclerView.setAdapter(exploreAdapter);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
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
                    @Override
                    public void onSuccess(List<User> allUsers) {
                        myFriendsList.clear();
                        for (User user : allUsers) {
                            if (friendIds.contains(user.getId())) {
                                myFriendsList.add(user);
                            }
                        }
                        myFriendsAdapter.updateList(myFriendsList);
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

    private void fetchExploreUsers() {
        DataUtils dataUtils = new DataUtils();
        String currentUserId = UserSession.getInstance().getCurrentUser().getId();

        dataUtils.getAll("users", User.class, new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> allUsers) {
                dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
                    @Override
                    public void onSuccess(List<Relationship> relationships) {
                        List<String> excludedUserIds = new ArrayList<>();
                        excludedUserIds.add(currentUserId); // Exclude the current user

                        for (Relationship relationship : relationships) {
                            if (relationship.getFirstUser().equals(currentUserId) || relationship.getSecondUser().equals(currentUserId)) {
                                excludedUserIds.add(relationship.getFirstUser().equals(currentUserId)
                                        ? relationship.getSecondUser()
                                        : relationship.getFirstUser());
                            }
                        }

                        exploreList.clear();
                        for (User user : allUsers) {
                            if (!excludedUserIds.contains(user.getId())) {
                                exploreList.add(user);
                            }
                        }
                        exploreAdapter.updateList(exploreList);
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
