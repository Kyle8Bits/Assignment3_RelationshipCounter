package com.example.assignment3_relationshipcounter.service.firestore;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.assignment3_relationshipcounter.service.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authentication {
    private FirebaseAuth mAuth;

    private DataUtils dataUtils;

    public Authentication() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    //newUser - please put the id for this to "N/A" when input
    public void registerNewUser(String email, String password, User newUser, RegisterNewUserCallback callback){

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            dataUtils.CreateNewUser(user.getUid(), newUser, new DataUtils.CreateNewUserCallback(){
                                @Override
                                public void onSuccess() {
                                    callback.onSuccess();
                                }

                                @Override
                                public void onFailure() {
                                    callback.onFailure();
                                }
                            });
                        }
                    } else {
                        callback.onFailure();
                    }
                }
            });

    }

    public interface RegisterNewUserCallback{
        void onSuccess();
        void onFailure();
    }
}
