package com.example.assignment3_relationshipcounter.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.bumptech.glide.Glide;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.EventAdapter;
import com.example.assignment3_relationshipcounter.main_screen.HomeActivity;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.Event;
import com.example.assignment3_relationshipcounter.service.models.FriendStatus;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.utils.CalendarEventHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FriendshipDetailFragment extends Fragment {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private MaterialButton optionButton;
    private MaterialButton backButton, viewGalleryButton;
    private MaterialButton callButton;
    private TextView daysCountTextView;

    private ShapeableImageView currentImage, friendImage;

    private User currentUser;
    private DataUtils dataUtils;
    private List<Event> sharedEvents;

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
        MaterialButton backButton = view.findViewById(R.id.back_button);
        optionButton = view.findViewById(R.id.option_button);
        viewGalleryButton = view.findViewById(R.id.view_gallery_button);
        daysCountTextView = view.findViewById(R.id.days_count);
        currentImage = view.findViewById(R.id.current_user_image);
        friendImage = view.findViewById(R.id.friend_image);
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        // Set up "View Gallery" button click listener
        viewGalleryButton.setOnClickListener(v -> navigateToGallery());
        setupOptionButton();
        setupCalendar(view);
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

        String friendAvatarURL = getArguments().getString("avatar");

        String currentAvatarURL = currentUser.getAvatarUrl();

        Glide.with(requireContext())
                .load(friendAvatarURL)
                .into(friendImage);

        Glide.with(requireContext())
                .load(currentAvatarURL)
                .into(currentImage);

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
            popupMenu.setForceShowIcon(true);
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


    private void setupCalendar(View view) {
        String relationshipId = getArguments().getString("relationshipId");
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        MaterialButton addEventBtn = view.findViewById(R.id.add_event);
        new DataUtils().getEvents(relationshipId, null, new DataUtils.FetchCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                sharedEvents = events;
                // Generate EventDays for the calendar
                List<EventDay> eventDays = CalendarEventHelper.generateEventDays(events);

                // Add EventDays to the calendar view
                calendarView.setEvents(eventDays);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });

        addEventBtn.setOnClickListener(v -> openAddEventBottomSheet(calendarView, sharedEvents));

        // Handle date clicks
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar selectedDate = eventDay.getCalendar();
            String selectedDateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime());
            fetchEventsForDate(selectedDateString);
        });
    }

    private void fetchEventsForDate(String selectedDate) {
        String relationshipId = getArguments().getString("relationshipId");

        new DataUtils().getEvents(relationshipId, selectedDate, new DataUtils.FetchCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                if (events.isEmpty()) {
                    Toast.makeText(requireContext(), "No events found for " + selectedDate, Toast.LENGTH_SHORT).show();
                } else {
                    for (Event event : events) {
                        event.setStatus(event.calculateStatus(event.getDate()));
                    }
                    displayEventsInBottomSheet(events);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void displayEventsInBottomSheet(List<Event> events) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_event_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView eventRecyclerView = bottomSheetView.findViewById(R.id.event_recycler_view);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        EventAdapter eventAdapter = new EventAdapter(events);
        eventRecyclerView.setAdapter(eventAdapter);

        bottomSheetDialog.show();
    }

    private void addEvent(String title, String description, String date, CalendarView calendarView, List<Event> events) {
        String relationshipId = getArguments().getString("relationshipId");
        Event event = new Event(title, description, date, relationshipId);

        new DataUtils().addEvent(event, new DataUtils.NormalCallback<Void>() {
            @Override
            public void onSuccess() {
                // Add the event to the local list and refresh the calendar
                events.add(event);
                refreshCalendar(calendarView, events);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to add event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openAddEventBottomSheet(CalendarView calendarView, List<Event> events) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_event_form, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Initialize UI components in the bottom sheet
        TextInputEditText eventTitle = bottomSheetView.findViewById(R.id.e_event_title);
        TextInputEditText eventDescription = bottomSheetView.findViewById(R.id.e_event_description);
        MaterialButton btnAddEvent = bottomSheetView.findViewById(R.id.action_add_event);
        TextView eventDate = bottomSheetView.findViewById(R.id.e_event_date);

        eventDate.setOnClickListener(v -> pickDate(eventDate));
        btnAddEvent.setOnClickListener(v -> {
            String title = eventTitle.getText().toString().trim();
            String description = eventDescription.getText().toString().trim();
            String date = eventDate.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                addEvent(title, description, date, calendarView, events);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }


    private void pickDate(TextView eDate) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String date = formatter.format(new Date(selection));
            eDate.setText(date);
        });

        datePicker.show(requireActivity().getSupportFragmentManager(), "tag");
    }

    private void refreshCalendar(CalendarView calendarView, List<Event> events) {
        List<EventDay> eventDays = CalendarEventHelper.generateEventDays(events);

        calendarView.setEvents(eventDays);
    }

}
