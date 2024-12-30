package com.example.assignment3_relationshipcounter.service.firestore;
import androidx.work.ListenableWorker;

import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DataUtils {

    private final FirebaseFirestore db;
    public DataUtils() {
        this.db = FirebaseFirestore.getInstance();
    }

    //NORMAL CRUD REQUEST

    public <T> void update(String collection, String id, T data, NormalCallback<T> callback) {
        db.collection(collection).document(id).set(data)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(new Exception("Cannot update data"));
                });
    }
    public <T extends HasId> void add(String collection, T data, NormalCallback<T> callback){
        DocumentReference docRef = db.collection(collection).document(); // Create reference with unique ID
        String generatedId = docRef.getId(); // Get the generated ID
        // Set the ID in the object
        data.setId(generatedId);
        // Add data to Firestore using the `set` method
        docRef.set(data)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(new Exception("Cannot add new data"));
                });
    }
    public <T> void delete(String collection, String id, NormalCallback<T> callback) {
        db.collection(collection).document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(new Exception("Cannot delete the data"));
                });
    }
    public <T> void get(String collection, String id, Class<T> object, GetCallback<T> callback) {
        db.collection(collection).document(id).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        T retrievedData = documentSnapshot.toObject(object);
                        if (retrievedData != null) {
                            callback.onSuccess(retrievedData);
                        } else {
                            callback.onFailure(new Exception("Data is null"));
                        }
                    }
                });
    }


    /**
     * Function use for update the daily counter
     * @see DailyFriendUpdateWorker
     * It called this function daily to updating the counter
     */

    public void updateCounterDaily( NormalCallback<Void> callback){
        db.collection("relationships")
            .whereEqualTo("status", "FRIEND") // Match only FRIEND status
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if(querySnapshot.isEmpty()){
                    return;
                }
                for (QueryDocumentSnapshot document : querySnapshot) {
                    // Get the current counter value
                    int currentCounter = document.getLong("counter").intValue();

                    // Increment the counter
                    db.collection("relationships")
                            .document(document.getId())
                            .update("counter", currentCounter + 1)
                            .addOnSuccessListener(aVoid -> {
                                System.out.println("Counter updated for document: " + document.getId());
                            })
                            .addOnFailureListener(e -> {
                                System.err.println("Failed to update counter for document: " + document.getId());
                            });
                }
            })
            .addOnFailureListener(e -> {
                System.err.println("Failed to fetch documents: " + e.getMessage());
            });
    }

    public interface NormalCallback<T> {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface GetCallback<T>{
        void onSuccess(T data);
        void onFailure(Exception e);
    }
    public interface HasId {
        void setId(String id);
    }


}
