package com.example.assignment3_relationshipcounter.service.firestore;

import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication {
    private final FirebaseAuth mAuth;
    private static FirebaseUser user;
    private static User userDetail = new User();
    private final DataUtils dataUtils = new DataUtils();

    public Authentication() {
        this.mAuth = FirebaseAuth.getInstance();
    }
    public FirebaseUser getFUser() {
        return user;
    }
    public void setUserDetail(User userDetail){
        Authentication.userDetail = userDetail;
    }
    public User getUserDetail(){
        return userDetail;
    }
    /**
     * @param email
     * @param password
     * @param newUser  - the ID attribute for User, please put "N/A"
     * @param callback - use the onSuccess to manage page (update UI, start Activity
     * @see DataUtils to add new user to Firestore with document ID as object ID
     */
    public void registerNewUser(String email, String password, User newUser, RegisterNewUserCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        newUser.setId(user.getUid());
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
                        callback.onFailure(task.getException());
                    }
                });


    }


    public void login(String email, String password, LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public interface LoginCallback {
        void onSuccess(FirebaseUser user);

        void onFailure(Exception e);
    }


    public interface RegisterNewUserCallback {
        void onSuccess();

        void onFailure(Exception e);
    }
}
