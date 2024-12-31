package com.example.assignment3_relationshipcounter.service.firestore;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;

import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

public class Authentication {
    private final FirebaseAuth mAuth;

    private static FirebaseUser user;

    private final DataUtils dataUtils = new DataUtils();

    public Authentication() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getFUser(){
        return user;
    }

    /**
     *
     * @param email
     * @param password
     * @param newUser - the ID attribute for User, please put "N/A"
     * @param callback - use the onSuccess to manage page (update UI, start Activity
     *
     * @see DataUtils to add new user to Firestore with document ID as object ID
     *
     */
    public void registerNewUser(String email, String password, User newUser, RegisterNewUserCallback callback){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        if (user != null) {
                            dataUtils.add("users", newUser, new DataUtils.NormalCallback<User>() {
                                @Override
                                public void onSuccess() {
                                    callback.onSuccess();
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    callback.onFailure(e);
                                }
                            });
                        }
                    } else {
                        callback.onFailure(new Exception("Cannot register new user"));
                    }
                }
            });
    }

    public interface RegisterNewUserCallback{
        void onSuccess();
        void onFailure(Exception e);
    }
}
