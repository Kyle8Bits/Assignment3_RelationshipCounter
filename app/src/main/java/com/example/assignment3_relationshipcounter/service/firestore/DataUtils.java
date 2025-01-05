package com.example.assignment3_relationshipcounter.service.firestore;

import android.util.Log;

import com.example.assignment3_relationshipcounter.service.models.ChatRoom;
import com.example.assignment3_relationshipcounter.service.models.Relationship;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DataUtils {

    private final FirebaseFirestore db;

    public DataUtils() {
        this.db = FirebaseFirestore.getInstance();
    }

    //NORMAL CRUD REQUEST

    /**
     *Update whole document or update one field in document using ID
     */
    public <T> void updateById(String collection, String id, T data, NormalCallback<T> callback) {
        db.collection(collection).document(id).set(data)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(new Exception("Cannot update data"));
                });
    }

    public <T> void updateOneFieldById(String collection, String id, String field, T value, NormalCallback<Void> callback) {
        db.collection(collection).document(id).update(field, value)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(new Exception("Cannot update data"));
                });
    }

    /**
     * Add new document, the object's id will be generated by Firestore
     */
    public <T extends HasId> void add(String collection, T data, NormalCallback<T> callback){
        DocumentReference docRef;

        if(data instanceof User){
            docRef = db.collection(collection).document(new Authentication().getFUser().getUid());
        }
        else {
           docRef = db.collection(collection).document();
        }

        String generatedId = docRef.getId(); // Get the generated ID
        // Set the ID in the object
        data.setId(generatedId);

        docRef.set(data)
            .addOnSuccessListener(aVoid -> {
                callback.onSuccess();
            })
            .addOnFailureListener(e -> {
                callback.onFailure(new Exception("Cannot add new data"));
            });
    }

    /**
     * Add a collection into a document
     * @param parentCollection the parent collection
     * @param parentDocumentID the document in the parent collection that you want to add
     * @param childCollection the collection that added into the parent document
     *
     * Use for "Chat function" do not delete
     */
    public <T> void addNewCollectionToDocument(String parentCollection, String parentDocumentID, String childCollection, T data, NormalCallback<T> callback) {
        db.collection(parentCollection).document(parentDocumentID).collection(childCollection).add(data)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e->{
                    callback.onFailure(new Exception("Cannot add new collection to document"));
                });
    }

    /**
     * Delete the document by ID
     */
    public <T> void deleteById(String collection, String id, NormalCallback<T> callback) {
        db.collection(collection).document(id).delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(new Exception("Cannot delete the data"));
                });
    }


    /**
     * Get one document by ID or get all document in collection (the callback on getAll return a list)
     */
    public <T> void getById(String collection, String id, Class<T> object, FetchCallback<T> callback) {
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


    public <T> void getAll(String collection, Class<T> objectClass, FetchCallback<List<T>> callback) {
        db.collection(collection).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<T> dataList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            T retrievedData = document.toObject(objectClass);
                            if (retrievedData != null) {
                                dataList.add(retrievedData);
                            }
                        }
                        callback.onSuccess(dataList); // Pass the list to onSuccess
                    } else {
                        callback.onSuccess(Collections.emptyList()); // Pass an empty list if no documents are found
                    }
                })
                .addOnFailureListener(callback::onFailure); // Pass the exception to onFailure
    }

    //THIS ONE IS IMPORTANT PLEASE DO NOT DELETE
    public Query getChatInChatroom(String chatRoomID){
        return db.collection("chatrooms").document(chatRoomID).collection("chats").orderBy("lastMessageTime", Query.Direction.DESCENDING);
    }

    public DocumentReference getAllChatRoomOfUser(List<String> userID){
        if(userID.get(0).equals(new Authentication().getFUser().getUid())){
            return db.collection("users").document(userID.get(1));
        }
        else{
            return db.collection("users").document(userID.get(0));
        }
    }

    public Query getAllChatRoomOfUser(){
        return db.collection("chatrooms").whereArrayContains("userIds", new Authentication().getFUser().getUid()).orderBy("lastMessageTime", Query.Direction.DESCENDING);
    }

    public void createNewChatRoom(String chatRoomId, ChatRoom chatRoom, NormalCallback<Void> callback){
        db.collection("chatrooms").document(chatRoomId).set(chatRoom)
                .addOnSuccessListener(
                        aVoid -> callback.onSuccess()
                )
                .addOnFailureListener(e -> {
                    System.out.println("Error retrieving document: " + e.getMessage());
                    callback.onFailure(e);
                });
    }

    public static String timestampToString(Timestamp timestamp) {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        return "";
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
                    int currentCounter = Objects.requireNonNull(document.getLong("counter")).intValue();

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

    public interface FetchCallback<T>{
        void onSuccess(T data);
        void onFailure(Exception e);
    }


    public interface HasId {
        void setId(String id);
    }

    public <T> void getByField(String collection, String field, String value, Class<T> objectClass, FetchCallback<List<T>> callback) {
        db.collection(collection)
                .whereEqualTo(field, value)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<T> dataList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            T retrievedData = document.toObject(objectClass);
                            if (retrievedData != null) {
                                dataList.add(retrievedData);
                            }
                        }
                        callback.onSuccess(dataList); // Pass the list to onSuccess
                    } else {
                        callback.onSuccess(Collections.emptyList()); // Pass an empty list if no documents are found
                    }
                })
                .addOnFailureListener(callback::onFailure); // Pass the exception to onFailure
    }

    public void addRelationship(Relationship relationship, NormalCallback<Void> callback) {
        // Create a new document reference to generate the ID
        DocumentReference docRef = db.collection("relationships").document();

        // Set the generated ID in the Relationship object
        relationship.setId(docRef.getId());

        // Save the relationship object to Firestore
        docRef.set(relationship)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(new Exception("Failed to add relationship"));
                });
    }

    /**
     * Updates an existing relationship in the Firestore database.
     *
     * @param relationship The relationship object to update.
     * @param callback     Callback to indicate success or failure.
     */
    public void updateRelationship(Relationship relationship, NormalCallback<Void> callback) {
        if (relationship == null || relationship.getId() == null || relationship.getId().isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid relationship object. ID is required."));
            return;
        }

        // Update the relationship in the Firestore database
        db.collection("relationships") // Use 'db' instead of 'firestore'
                .document(relationship.getId())
                .set(relationship)
                .addOnSuccessListener(unused -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onFailure(e);
                });
    }

}
