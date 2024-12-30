package com.example.assignment3_relationshipcounter.service.firestore;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DataUtils {

    private final FirebaseFirestore db;

    public DataUtils() {
        this.db = FirebaseFirestore.getInstance();
    }

    //CRUD

    //Add new user to database
    public void CreateNewUser(String documentID, User newUser, CreateNewUserCallback callback){
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Get the current number of documents in the collection
                    int count = task.getResult().size();
                    // Generate a custom ID
                    String customId = "U+" + (count + 1);

                   newUser.setId(customId);

                    // Save the user document with the custom ID
                    db.collection("users").document(documentID).set(newUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    callback.onSuccess();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    callback.onFailure();
                                }
                            });

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
    public interface CreateNewUserCallback{
        void onSuccess();
        void onFailure();
    }

    //Get the current user data


}
