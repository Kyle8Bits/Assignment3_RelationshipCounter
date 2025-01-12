package com.example.assignment3_relationshipcounter.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.main_screen.PaymentActivity;
import com.example.assignment3_relationshipcounter.main_screen.WelcomeActivity;
import com.example.assignment3_relationshipcounter.service.firestore.Authentication;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.service.models.UserType;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileFragment extends Fragment {
    private ActivityResultLauncher<Intent> paymentLauncher;
    private ImageView avatar;
    private EditText firstName, lastName, email, phoneNumber, username;
    private TextView dob;
    private MaterialButton upgradeBtn;
    private String storageUrl;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Button logOutBtn = view.findViewById(R.id.action_logout);
        logOutBtn.setOnClickListener(v -> logout());

        // Get profile fields
        avatar = view.findViewById(R.id.avatar);
        firstName = view.findViewById(R.id.e_first_name);
        lastName = view.findViewById(R.id.e_last_name);
        username = view.findViewById(R.id.e_username);
        email = view.findViewById(R.id.email);
        dob = view.findViewById(R.id.e_dob);
        phoneNumber = view.findViewById(R.id.e_phone_number);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        loadProfile();

        avatar.setOnClickListener(v -> pickImage());
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> cancelChanges());
        upgradeBtn = view.findViewById(R.id.upgrade_to_premium_button);
        upgradeBtn.setOnClickListener(v -> navigateToPaymentActivity());
        if (user.getAccountType() == UserType.PREMIUM) {
            upgradeBtn.setText("Premium user");
            upgradeBtn.setEnabled(false);
        }
        if(user.getAccountType() == UserType.ADMIN){
            upgradeBtn.setText("Admin");
            upgradeBtn.setEnabled(false);
        }
        // Register ActivityResultLauncher
        paymentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        boolean paymentSuccess = result.getData().getBooleanExtra("paymentSuccess", false);
                        if (paymentSuccess) {
                            upgradeBtn.setText("Premium user");
                            upgradeBtn.setEnabled(false);
                        }
                    }
                }
        );


        return view;
    }

    private void logout() {
        // Navigate back to main
        Authentication auth = new Authentication();
        auth.logout();
        Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
        // Clear back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }


    private void loadProfile() {
        User user = (User) getArguments().getSerializable("user");
        if (user != null) {
            this.user = user;
            firstName.setText(user.getFirstName());
            lastName.setText(user.getLastName());
            email.setText(user.getEmail());
            dob.setText(user.getDoB());
            username.setText(user.getUsername());
            phoneNumber.setText(user.getPhoneNumber());
            Glide.with(requireContext())
                    .load(user.getAvatarUrl())
                    .placeholder(R.drawable.sample)
                    .circleCrop()
                    .into(avatar);
        }
    }

    private void pickImage() {
        avatar.setOnClickListener(view ->
                ImagePicker.with(this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(512, 512)
                        .createIntent(intent -> {
                            startActivityForResult(intent, 1001);
                            return null;
                        }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK && data != null) {
                Uri imageURI = data.getData();
                avatar.setImageURI(imageURI);
                uploadToFirebase(imageURI, this::saveProfile);
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                String errorMessage = ImagePicker.getError(data);
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireActivity(), "Canceled choosing avatar", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadToFirebase(Uri imageUri, Runnable onSuccess) {

        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReference("avatars/" + user.getId());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    this.storageUrl = uri.toString();
                    onSuccess.run();
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveProfile() {

        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        user.setUsername(username.getText().toString());
        user.setDoB(dob.getText().toString());
        user.setPhoneNumber(phoneNumber.getText().toString());
        user.setAvatarUrl(this.storageUrl);

        new DataUtils().updateById("users", user.getId(), user, new DataUtils.NormalCallback<User>() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFields(User user) {
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        dob.setText(user.getDoB());
        username.setText(user.getUsername());
        phoneNumber.setText(user.getPhoneNumber());
        Glide.with(requireContext())
                .load(user.getAvatarUrl())
                .centerCrop()
                .into(avatar);
    }


    // Revert input fields to the original user data
    private void cancelChanges() {
        if (user != null) {
            loadFields(user);
        }
    }

    private void navigateToPaymentActivity() {
        Intent intent = new Intent(requireContext(), PaymentActivity.class);
        paymentLauncher.launch(intent);
    }


}