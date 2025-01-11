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
import com.google.firebase.Timestamp;

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

    private MaterialButton searchButton, filterButton;
    private DataUtils dataUtils;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize DataUtils
        dataUtils = new DataUtils();

        // Initialize UI components
        storyRecyclerView = view.findViewById(R.id.home_story_list);
        tabRecyclerView = view.findViewById(R.id.home_recycler_view);
        tabLayout = view.findViewById(R.id.home_tab_layout);
        searchButton = view.findViewById(R.id.search_button);
        filterButton = view.findViewById(R.id.filter_button);

        // Load Stories
        loadStories();

        // Load Tab Content
        setupTabs();

        // Set up click listener for the search button
        searchButton.setOnClickListener(v -> navigateToSearchFriendFragment());

        // Filter button listener
        filterButton.setOnClickListener(v -> {
            FilterBottomSheetFragment bottomSheet = new FilterBottomSheetFragment();
            bottomSheet.setFilterCallback(new FilterBottomSheetFragment.FilterCallback() {
                @Override
                public void onFilterByLatestDays() {
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        fetchMyFriends(true, true, false, false); // Sort by latest date
                    } else {
                        fetchFriendRequests(true, true, false, false); // Sort by latest date
                    }
                }

                @Override
                public void onFilterByOldestDays() {
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        fetchMyFriends(true, false, false, false); // Sort by oldest date
                    } else {
                        fetchFriendRequests(true, false, false, false); // Sort by oldest date
                    }
                }

                @Override
                public void onFilterByNameAZ() {
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        fetchMyFriends(false, false, true, true); // Sort by name A-Z
                    } else {
                        fetchFriendRequests(false, false, true, true); // Sort by name A-Z
                    }
                }

                @Override
                public void onFilterByNameZA() {
                    if (tabLayout.getSelectedTabPosition() == 0) {
                        fetchMyFriends(false, false, true, false); // Sort by name Z-A
                    } else {
                        fetchFriendRequests(false, false, true, false); // Sort by name Z-A
                    }
                }
            });
            bottomSheet.show(requireActivity().getSupportFragmentManager(), "FilterBottomSheet");
        });


        return view;
    }



    private void filterMyFriendsByDays(boolean latestFirst) {
//        if (myFriendsList != null) {
//            // Sort relationships based on the creation date
//            myFriendsList.sort((u1, u2) -> {
//                Relationship relationship1 = findRelationshipByUser(u1.getId());
//                Relationship relationship2 = findRelationshipByUser(u2.getId());
//                if (relationship1 != null && relationship2 != null) {
//                    Timestamp date1 = relationship1.getDateCreated();
//                    Timestamp date2 = relationship2.getDateCreated();
//                    return latestFirst ? date2.compareTo(date1) : date1.compareTo(date2);
//                }
//                return 0; // Keep the order if relationships are missing
//            });
//
//            // Update the adapter
//            myFriendsAdapter.updateList(myFriendsList);
//
//            // Log the filtered list
//            Log.d("FilterMyFriendsByDays", "Filtered My Friends (Days):");
//            for (User user : myFriendsList) {
//                Log.d("FilterMyFriendsByDays", "User: " + user.getUsername() + " (ID: " + user.getId() + ")");
//            }
//        }
    }



    private void filterRequestsByDays(boolean latestFirst) {
        String currentUserId = UserSession.getInstance().getCurrentUser().getId();
        dataUtils.filterFriendRequestsByDays(currentUserId, latestFirst, new DataUtils.FetchCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                requestList = data;
                requestAdapter.updateList(requestList);

                // Log user list
                Log.d("FilterMyFriendsByDays", "Filtered My Friends (Days):");
                for (User user : myFriendsList) {
                    Log.d("FilterMyFriendsByDays", "User: " + user.getUsername() + " (ID: " + user.getId() + ")");
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void filterMyFriendsByName(boolean ascending) {
        if (myFriendsList != null) {
            // Sort by username (ascending or descending)
            myFriendsList.sort((u1, u2) -> ascending
                    ? u1.getUsername().compareToIgnoreCase(u2.getUsername())
                    : u2.getUsername().compareToIgnoreCase(u1.getUsername()));

            // Update the adapter
            myFriendsAdapter.updateList(myFriendsList);

            // Log the filtered list
            Log.d("FilterMyFriendsByName", "Filtered My Friends (Name):");
            for (User user : myFriendsList) {
                Log.d("FilterMyFriendsByName", "User: " + user.getUsername() + " (ID: " + user.getId() + ")");
            }
        }
    }

    private void filterRequestsByName(boolean ascending) {
        if (requestList != null) {
            // Sort by username (ascending or descending)
            requestList.sort((u1, u2) -> ascending
                    ? u1.getUsername().compareToIgnoreCase(u2.getUsername())
                    : u2.getUsername().compareToIgnoreCase(u1.getUsername()));

            // Update the adapter
            requestAdapter.updateList(requestList);

            // Log the filtered list
            Log.d("FilterRequestsByName", "Filtered Requests (Name):");
            for (User user : requestList) {
                Log.d("FilterRequestsByName", "User: " + user.getUsername() + " (ID: " + user.getId() + ")");
            }
        }
    }

    private void navigateToSearchFriendFragment() {
        // Create a new instance of SearchFriendFragment
        SearchFriendFragment searchFriendFragment = new SearchFriendFragment();

        // Replace the current fragment with SearchFriendFragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, searchFriendFragment);
        transaction.addToBackStack(null);
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

        // Set RecyclerView properties
        tabRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set default tab
        tabLayout.selectTab(tabLayout.getTabAt(0));

        // Fetch Data for My Friends Tab (default sort by name A-Z)
        tabRecyclerView.setAdapter(myFriendsAdapter);
        fetchMyFriends(false, false, true, true); // Default sort by Name A-Z

        // Add Tab Selection Logic
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // Switch to My Friends Tab
                    tabRecyclerView.setAdapter(myFriendsAdapter);
                    fetchMyFriends(false, false, true, true); // Default sort by Name A-Z
                } else if (tab.getPosition() == 1) {
                    // Switch to Requests Tab
                    tabRecyclerView.setAdapter(requestAdapter);
                    fetchFriendRequests(false, false, true, true); // Default sort by Name A-Z
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Force refresh when a tab is reselected
                if (tab.getPosition() == 0) {
                    fetchMyFriends(false, false, true, true); // Default sort by Name A-Z
                } else if (tab.getPosition() == 1) {
                    fetchFriendRequests(false, false, true, true); // Default sort by Name A-Z
                }
            }
        });
    }

    private void fetchMyFriends(boolean sortByDate, boolean latestFirst, boolean sortByName, boolean ascending) {
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
                            }
                        }

                        if (sortByDate) {
                            myFriendsList.sort((u1, u2) -> {
                                Relationship rel1 = findRelationshipByUser(u1.getId(), relationships);
                                Relationship rel2 = findRelationshipByUser(u2.getId(), relationships);
                                if (rel1 != null && rel2 != null) {
                                    Timestamp date1 = rel1.getDateCreated();
                                    Timestamp date2 = rel2.getDateCreated();
                                    return latestFirst ? date2.compareTo(date1) : date1.compareTo(date2);
                                }
                                return 0; // Keep order if relationships are missing
                            });
                        } else if (sortByName) {
                            myFriendsList.sort((u1, u2) -> ascending
                                    ? u1.getUsername().compareToIgnoreCase(u2.getUsername())
                                    : u2.getUsername().compareToIgnoreCase(u1.getUsername()));
                        }

                        myFriendsAdapter.updateList(myFriendsList);
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

    private Relationship findRelationshipByUser(String userId, List<Relationship> relationships) {
        for (Relationship relationship : relationships) {
            if (relationship.getFirstUser().equals(userId) || relationship.getSecondUser().equals(userId)) {
                return relationship;
            }
        }
        return null; // Return null if no matching relationship is found
    }

    private void fetchFriendRequests(boolean sortByDate, boolean latestFirst, boolean sortByName, boolean ascending) {
        String currentUserId = UserSession.getInstance().getCurrentUser().getId();

        dataUtils.getAll("relationships", Relationship.class, new DataUtils.FetchCallback<List<Relationship>>() {
            @Override
            public void onSuccess(List<Relationship> relationships) {
                List<String> requestSenderIds = new ArrayList<>();

                for (Relationship relationship : relationships) {
                    if (relationship.getSecondUser().equals(currentUserId) && relationship.getStatus() == FriendStatus.PENDING) {
                        requestSenderIds.add(relationship.getFirstUser());
                    }
                }

                dataUtils.getAll("users", User.class, new DataUtils.FetchCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> allUsers) {
                        requestList.clear();
                        for (User user : allUsers) {
                            if (requestSenderIds.contains(user.getId())) {
                                requestList.add(user); // Add users who sent friend requests
                            }
                        }

                        if (sortByDate) {
                            requestList.sort((u1, u2) -> {
                                Relationship rel1 = findRelationshipByUser(u1.getId(), relationships);
                                Relationship rel2 = findRelationshipByUser(u2.getId(), relationships);
                                if (rel1 != null && rel2 != null) {
                                    Timestamp date1 = rel1.getDateCreated();
                                    Timestamp date2 = rel2.getDateCreated();
                                    return latestFirst ? date2.compareTo(date1) : date1.compareTo(date2);
                                }
                                return 0; // Keep order if relationships are missing
                            });
                        } else if (sortByName) {
                            requestList.sort((u1, u2) -> ascending
                                    ? u1.getUsername().compareToIgnoreCase(u2.getUsername())
                                    : u2.getUsername().compareToIgnoreCase(u1.getUsername()));
                        }

                        requestAdapter.updateList(requestList);
                        requestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("FetchRequests", "Failed to fetch users", e);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FetchRequests", "Failed to fetch relationships", e);
            }
        });
    }

}
