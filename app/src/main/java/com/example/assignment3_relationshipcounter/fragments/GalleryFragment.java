package com.example.assignment3_relationshipcounter.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.adapter.GalleryAdapter;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.GalleryItem;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryFragment extends Fragment {

    private String relationshipId;
    private RecyclerView galleryRecyclerView;
    private GalleryAdapter galleryAdapter;
    private List<GalleryItem> galleryItemList;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    private ImageView selectedImageView; // Maintain reference for the selected image preview

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Initialize Firestore and Storage
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("gallery");

        // Get relationshipId from arguments
        if (getArguments() != null) {
            relationshipId = getArguments().getString("relationshipId");
        }

        // Initialize UI components
        galleryRecyclerView = view.findViewById(R.id.gallery_recycler_view);
        MaterialButton addButton = view.findViewById(R.id.add_button);
        MaterialButton backButton = view.findViewById(R.id.back_button);

        galleryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        galleryItemList = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(galleryItemList);
        galleryRecyclerView.setAdapter(galleryAdapter);

        // Load gallery items
        loadGalleryItems();

        // Add button click listener
        addButton.setOnClickListener(v -> openAddImageDialog());

        // Back button click listener
        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack(); // Navigate to the previous fragment
            } else {
                requireActivity().onBackPressed(); // Exit activity if no fragments in backstack
            }
        });

        return view;
    }

    private void loadGalleryItems() {
        if (relationshipId == null) {
            Toast.makeText(requireContext(), "Invalid relationship ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        DataUtils dataUtils = new DataUtils();
        dataUtils.loadGalleryItems(relationshipId, new DataUtils.FetchCallback<List<GalleryItem>>() {
            @Override
            public void onSuccess(List<GalleryItem> data) {
                galleryItemList.clear();
                galleryItemList.addAll(data);
                galleryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Failed to load gallery items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddImageDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_image, null);
        EditText descriptionInput = dialogView.findViewById(R.id.description_input);
        selectedImageView = dialogView.findViewById(R.id.selected_image); // Assign reference
        MaterialButton pickImageButton = dialogView.findViewById(R.id.pick_image_button);
        MaterialButton saveButton = dialogView.findViewById(R.id.save_button);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        pickImageButton.setOnClickListener(v -> pickImage());

        saveButton.setOnClickListener(v -> {
            String description = descriptionInput.getText().toString().trim();
            if (selectedImageUri == null || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please select an image and enter a description.", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadImage(selectedImageUri, description, dialog);
        });

        dialog.show();
    }

    private void pickImage() {
        ImagePicker.with(this)
                .crop() // Optional: Crop the selected image
                .compress(1024) // Compress image to 1MB
                .maxResultSize(1080, 1080) // Resize image
                .createIntent(intent -> {
                    startActivityForResult(intent, 1001);
                    return null;
                });

        selectedImageUri = null; // Reset the selected image URI
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null && selectedImageView != null) {
                Glide.with(requireContext())
                        .load(selectedImageUri)
                        .into(selectedImageView);
            } else {
                Toast.makeText(requireContext(), "Failed to load image preview.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Image selection failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri, String description, AlertDialog dialog) {
        if (relationshipId == null) {
            Toast.makeText(requireContext(), "Invalid relationship ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageReference.child(relationshipId + "/" + fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    GalleryItem item = new GalleryItem(
                            null, // ID will be auto-generated by Firestore
                            relationshipId,
                            uri.toString(),
                            description,
                            new Timestamp(new Date())
                    );

                    firestore.collection("gallery").add(item)
                            .addOnSuccessListener(documentReference -> {
                                item.setId(documentReference.getId());
                                galleryItemList.add(0, item); // Add item to the top of the list
                                galleryAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                                Toast.makeText(requireContext(), "Image added successfully.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Failed to save image to Firestore.", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to upload image to Firebase Storage.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}
