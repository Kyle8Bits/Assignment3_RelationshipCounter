//package com.example.assignment3_relationshipcounter.fragments;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.assignment3_relationshipcounter.R;
//import com.example.assignment3_relationshipcounter.adapter.EventAdapter;
//import com.example.assignment3_relationshipcounter.service.models.Event;
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EventFragment extends BottomSheetDialogFragment {
//    private RecyclerView eventRecyclerView;
//    private EventAdapter eventAdapter;
//    private List<Event> events;
//
//    public static EventFragment newInstance(List<Event> events) {
//        EventFragment fragment = new EventFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("events", new ArrayList<>(events));
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_event_bottom_sheet, container, false);
//
//        // Initialize RecyclerView
//        eventRecyclerView = view.findViewById(R.id.event_recycler_view);
//        eventRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//        // Retrieve events from arguments
//        events = (List<Event>) getArguments().getSerializable("events");
//        eventAdapter = new EventAdapter(events);
//        eventRecyclerView.setAdapter(eventAdapter);
//
//        return view;
//    }
//}
//
